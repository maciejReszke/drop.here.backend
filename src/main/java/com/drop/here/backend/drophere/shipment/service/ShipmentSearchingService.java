package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.service.CompanyService;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.service.CustomerSearchingService;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.service.DropSearchingService;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import com.drop.here.backend.drophere.product.service.ProductCustomizationService;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProductCustomizationResponse;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProductResponse;
import com.drop.here.backend.drophere.shipment.dto.ShipmentResponse;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProductCustomization;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.repository.ShipmentProductCustomizationRepository;
import com.drop.here.backend.drophere.shipment.repository.ShipmentProductRepository;
import com.drop.here.backend.drophere.shipment.repository.ShipmentRepository;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentSearchingService {
    private final ShipmentRepository shipmentRepository;
    private final ShipmentProductRepository productRepository;
    private final DropSearchingService dropSearchingService;
    private final CompanyService companyService;
    private final CustomerSearchingService customerSearchingService;
    private final ShipmentProductCustomizationRepository shipmentProductCustomizationRepository;
    private final ProductCustomizationService productCustomizationService;
    private final ShipmentPersistenceService shipmentPersistenceService;

    public ShipmentResponse findCustomerShipment(Customer customer, Long shipmentId) {
        final Shipment shipment = shipmentPersistenceService.findShipment(shipmentId, customer);
        return mapToShipmentResponses(new PageImpl<>(List.of(shipment)))
                .stream()
                .findFirst()
                .orElseThrow();
    }

    public Page<ShipmentResponse> findCustomerShipments(Customer customer, String status, Pageable pageable) {
        final ShipmentStatus shipmentStatus = parseOrNull(status);
        final Page<Shipment> shipments = shipmentRepository.findByCustomerAndStatus(customer, shipmentStatus, pageable);
        return mapToShipmentResponses(shipments);
    }

    public Page<ShipmentResponse> findCompanyShipments(Company company, String status, Long routeId, String dropUid, Pageable pageable) {
        final ShipmentStatus shipmentStatus = parseOrNull(status);
        final Page<Shipment> shipments = shipmentRepository.findByCompanyAndStatusAndRouteIdAndDropUid(company, shipmentStatus, routeId, dropUid, pageable);
        return mapToShipmentResponses(shipments);
    }

    public ShipmentResponse findCompanyShipment(Company company, Long shipmentId) {
        final Shipment shipment = shipmentPersistenceService.findShipment(shipmentId, company);
        return mapToShipmentResponses(new PageImpl<>(List.of(shipment)))
                .stream()
                .findFirst()
                .orElseThrow();
    }

    private Page<ShipmentResponse> mapToShipmentResponses(Page<Shipment> shipmentsPage) {
        final List<Shipment> shipments = shipmentsPage.toList();
        final List<Drop> drops = findDrops(shipments);
        final List<Company> companies = findCompanies(shipments);
        final List<Customer> customers = findCustomers(shipments);
        final List<ShipmentProduct> shipmentProducts = findShipmentProducts(shipments);
        final List<ShipmentProductCustomization> shipmentProductCustomizations = findShipmentProductCustomizations(shipmentProducts);
        final List<ProductCustomization> customizations = findCustomizations(shipmentProductCustomizations);
        return shipmentsPage.map(shipment -> toShipmentResponse(drops, companies, customers, shipmentProducts, shipmentProductCustomizations, customizations, shipment));
    }

    private List<Customer> findCustomers(List<Shipment> shipments) {
        final List<Long> customersIds = shipments
                .stream()
                .map(Shipment::getCustomer)
                .map(Customer::getId)
                .collect(Collectors.toList());

        return customerSearchingService.findCustomers(customersIds);
    }

    private ShipmentResponse toShipmentResponse(List<Drop> drops, List<Company> companies, List<Customer> customers, List<ShipmentProduct> shipmentProducts, List<ShipmentProductCustomization> shipmentProductCustomizations, List<ProductCustomization> customizations, Shipment shipment) {
        final List<ShipmentProduct> productsForShipment = findProductsForShipment(shipment, shipmentProducts);
        final Set<Long> productsIds = productsForShipment.stream()
                .map(ShipmentProduct::getId)
                .collect(Collectors.toSet());
        final List<ShipmentProductCustomization> productCustomizations = findShipmentProductCustomizationsForShipment(productsIds, shipmentProductCustomizations);
        final Set<Long> customizationsIds = productCustomizations.stream()
                .map(customization -> customization.getProductCustomization().getId())
                .collect(Collectors.toSet());
        final List<ProductCustomization> customizationsForShipment = findProductCustomizationsForShipment(customizationsIds, customizations);
        return toShipmentCustomerResponse(
                shipment,
                findDropForShipment(shipment, drops),
                findCompanyForShipment(shipment, companies),
                findCustomerForShipment(shipment, customers),
                productsForShipment,
                productCustomizations,
                customizationsForShipment
        );
    }

    private Customer findCustomerForShipment(Shipment shipment, List<Customer> customers) {
        return customers.stream()
                .filter(c -> c.getId().equals(shipment.getCustomer().getId()))
                .findFirst()
                .orElseThrow();
    }

    private List<ProductCustomization> findProductCustomizationsForShipment(Set<Long> customizationsIds, List<ProductCustomization> customizations) {
        return customizations.stream()
                .filter(t -> customizationsIds.contains(t.getId()))
                .collect(Collectors.toList());
    }

    private List<ShipmentProductCustomization> findShipmentProductCustomizationsForShipment(Set<Long> productsIds, List<ShipmentProductCustomization> shipmentProductCustomizations) {
        return shipmentProductCustomizations.stream()
                .filter(customization -> productsIds.contains(customization.getShipmentProduct().getId()))
                .collect(Collectors.toList());
    }

    private List<ShipmentProduct> findProductsForShipment(Shipment shipment, List<ShipmentProduct> shipmentProducts) {
        return shipmentProducts.stream()
                .filter(product -> product.getShipment().getId().equals(shipment.getId()))
                .sorted(Comparator.comparing(ShipmentProduct::getOrderNum, Comparator.naturalOrder()))
                .collect(Collectors.toList());
    }

    private List<ProductCustomization> findCustomizations(List<ShipmentProductCustomization> shipmentProductCustomizations) {
        final List<Long> customizationsIds = shipmentProductCustomizations.stream()
                .map(ShipmentProductCustomization::getProductCustomization)
                .map(ProductCustomization::getId)
                .collect(Collectors.toList());
        return productCustomizationService.findCustomizationsWithWrapper(customizationsIds);
    }

    private List<ShipmentProductCustomization> findShipmentProductCustomizations(List<ShipmentProduct> shipmentProducts) {
        final List<Long> customizationsIds = shipmentProducts.stream()
                .map(ShipmentProduct::getCustomizations)
                .flatMap(Collection::stream)
                .map(ShipmentProductCustomization::getId)
                .collect(Collectors.toList());
        return shipmentProductCustomizationRepository.findAllById(customizationsIds);
    }

    private List<ShipmentProduct> findShipmentProducts(List<Shipment> shipments) {
        final List<Long> productsIds = shipments.stream()
                .map(Shipment::getProducts)
                .flatMap(Collection::stream)
                .map(ShipmentProduct::getId)
                .collect(Collectors.toList());

        return productRepository.findByIdWithProduct(productsIds);
    }

    private Company findCompanyForShipment(Shipment shipment, List<Company> companies) {
        return companies.stream()
                .filter(company -> shipment.getCompany().getId().equals(company.getId()))
                .findFirst()
                .orElseThrow();
    }

    private List<Company> findCompanies(List<Shipment> shipments) {
        final List<Long> companiesIds = shipments
                .stream()
                .map(Shipment::getCompany)
                .map(Company::getId)
                .collect(Collectors.toList());

        return companyService.findCompanies(companiesIds);
    }

    private Drop findDropForShipment(Shipment shipment, List<Drop> drops) {
        return drops.stream()
                .filter(drop -> shipment.getDrop().getId().equals(drop.getId()))
                .findFirst()
                .orElseThrow();
    }


    private List<Drop> findDrops(List<Shipment> shipments) {
        final List<Long> dropsIds = shipments
                .stream()
                .map(Shipment::getDrop)
                .map(Drop::getId)
                .collect(Collectors.toList());
        return dropSearchingService.findDrops(dropsIds);
    }

    private ShipmentResponse toShipmentCustomerResponse(Shipment shipment, Drop drop, Company company, Customer customer, List<ShipmentProduct> shipmentProducts, List<ShipmentProductCustomization> shipmentProductCustomizations, List<ProductCustomization> productCustomizations) {
        return ShipmentResponse.builder()
                .id(shipment.getId())
                .status(shipment.getStatus())
                .dropUid(drop.getUid())
                .companyName(company.getName())
                .companyUid(company.getUid())
                .customerFirstName(customer.getFirstName())
                .customerLastName(customer.getLastName())
                .customerId(customer.getId())
                .createdAt(optionalDatetime(shipment.getCreatedAt()))
                .placedAt(optionalDatetime(shipment.getPlacedAt()))
                .acceptedAt(optionalDatetime(shipment.getAcceptedAt()))
                .cancelledAt(optionalDatetime(shipment.getCancelledAt()))
                .cancelRequestedAt(optionalDatetime(shipment.getCancelRequestedAt()))
                .deliveredAt(optionalDatetime(shipment.getDeliveredAt()))
                .rejectedAt(optionalDatetime(shipment.getRejectedAt()))
                .products(toShipmentProductResponses(shipmentProducts, shipmentProductCustomizations, productCustomizations))
                .summarizedAmount(shipment.getSummarizedAmount())
                .customerComment(shipment.getCustomerComment())
                .companyComment(shipment.getCompanyComment())
                .build();
    }

    private List<ShipmentProductResponse> toShipmentProductResponses(List<ShipmentProduct> shipmentProducts, List<ShipmentProductCustomization> productCustomizations, List<ProductCustomization> productCustomizationsForShipment) {
        return shipmentProducts.stream()
                .map(shipmentProduct -> toShipmentProductResponse(
                        shipmentProduct,
                        findShipmentProductCustomizationsForProduct(shipmentProduct, productCustomizations),
                        productCustomizationsForShipment))
                .collect(Collectors.toList());
    }

    private ShipmentProductResponse toShipmentProductResponse(ShipmentProduct shipmentProduct, List<ShipmentProductCustomization> shipmentProductCustomizations, List<ProductCustomization> productCustomizations) {
        final Product product = shipmentProduct.getProduct();
        return ShipmentProductResponse.builder()
                .id(shipmentProduct.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productDescription(product.getDescription())
                .customizations(toShipmentProductCustomizationResponses(shipmentProductCustomizations, productCustomizations))
                .unitPrice(shipmentProduct.getUnitPrice())
                .unitCustomizationsPrice(shipmentProduct.getUnitCustomizationsPrice())
                .unitSummarizedPrice(shipmentProduct.getUnitSummarizedPrice())
                .summarizedPrice(shipmentProduct.getSummarizedPrice())
                .quantity(shipmentProduct.getQuantity())
                .build();
    }

    private List<ShipmentProductCustomizationResponse> toShipmentProductCustomizationResponses(List<ShipmentProductCustomization> shipmentProductCustomizations, List<ProductCustomization> productCustomizations) {
        return shipmentProductCustomizations.stream()
                .map(shipmentProductCustomization -> toShipmentProductCustomizationResponse(shipmentProductCustomization, productCustomizations))
                .collect(Collectors.toList());
    }

    private ShipmentProductCustomizationResponse toShipmentProductCustomizationResponse(ShipmentProductCustomization shipmentProductCustomization, List<ProductCustomization> productCustomizations) {
        final ProductCustomization productCustomization = productCustomizations.stream()
                .filter(customization -> customization.getId().equals(shipmentProductCustomization.getProductCustomization().getId()))
                .findFirst()
                .orElseThrow();
        return ShipmentProductCustomizationResponse.builder()
                .wrapperId(productCustomization.getWrapper().getId())
                .wrapperHeading(productCustomization.getWrapper().getHeading())
                .wrapperType(productCustomization.getWrapper().getType())
                .customizationPrice(shipmentProductCustomization.getPrice())
                .customizationValue(productCustomization.getValue())
                .build();
    }

    private List<ShipmentProductCustomization> findShipmentProductCustomizationsForProduct(ShipmentProduct shipmentProduct, List<ShipmentProductCustomization> productCustomizations) {
        return productCustomizations.stream()
                .filter(productCustomization -> productCustomization.getShipmentProduct().getId().equals(shipmentProduct.getId()))
                .collect(Collectors.toList());
    }

    private String optionalDatetime(LocalDateTime placedAt) {
        return Optional.ofNullable(placedAt)
                .map(DateTimeFormatter.ISO_LOCAL_DATE_TIME::format)
                .orElse(null);
    }

    private ShipmentStatus parseOrNull(String status) {
        return Try.ofSupplier(() -> ShipmentStatus.valueOf(status))
                .getOrElse(() -> null);
    }
}
