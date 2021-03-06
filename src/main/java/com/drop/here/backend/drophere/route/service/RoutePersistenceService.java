package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.route.dto.RouteShortResponse;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import com.drop.here.backend.drophere.route.repository.RouteRepository;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoutePersistenceService {
    private final RouteRepository routeRepository;

    public void save(Route route) {
        routeRepository.save(route);
    }

    public Optional<Route> findByIdAndCompany(Long routeId, Company company) {
        return routeRepository.findByIdAndCompany(routeId, company);
    }

    public void delete(Route route) {
        routeRepository.delete(route);
    }

    public Page<RouteShortResponse> findByCompany(Company company, String routeStatus, Pageable pageable) {
        return routeRepository.findByCompany(company, parseOrNull(routeStatus), pageable);
    }

    private RouteStatus parseOrNull(String status) {
        return Try.ofSupplier(() -> RouteStatus.valueOf(status))
                .getOrElse(() -> null);
    }

    public boolean existsByStatusAndProfile(RouteStatus status, AccountProfile profile) {
        return routeRepository.existsByStatusAndProfile(status, profile);
    }

    public List<Route> finishToBeFinished() {
        return routeRepository.finishToBeFinished();
    }

    public Route findLocked(Long routeId) {
        return routeRepository.findByIdWithLock(routeId)
                .orElseThrow();
    }

    public List<Route> finishObsolete() {
        return routeRepository.finishObsolete();
    }
}
