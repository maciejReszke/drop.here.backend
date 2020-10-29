package com.drop.here.backend.drophere.route.scheduler;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import com.drop.here.backend.drophere.route.repository.RouteRepository;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import com.drop.here.backend.drophere.test_data.RouteDataGenerator;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RouteFinishingSchedulerTest extends IntegrationBaseClass {


    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountProfileRepository accountProfileRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private SpotRepository spotRepository;

    @Autowired
    private DropRepository dropRepository;

    @Autowired
    private RouteFinishingScheduler routeFinishingScheduler;

    private Company company;
    private Account account;
    private Spot spot;

    @BeforeEach
    void prepare() {
        final Country country = countryRepository.save(CountryDataGenerator.poland());
        account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        spot = spotRepository.save(SpotDataGenerator.spot(1, company));
    }

    @AfterEach
    void cleanUp() {
        routeRepository.deleteAll();
        spotRepository.deleteAll();
        dropRepository.deleteAll();
        companyRepository.deleteAll();
        accountProfileRepository.deleteAll();
        accountRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void givenFinishedDropNotFinishedRouteWhenFinishFinishedThenFinish() {
        //given
        final Route route = RouteDataGenerator.route(1, company);
        route.setStatus(RouteStatus.ONGOING);
        routeRepository.save(route);
        final Drop drop = DropDataGenerator.drop(1, route, spot);
        drop.setStatus(DropStatus.FINISHED);
        dropRepository.save(drop);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStatus(DropStatus.FINISHED);
        dropRepository.save(drop2);

        //when
        routeFinishingScheduler.finishFinished();

        //then
        final Route savedRoute = routeRepository.findById(route.getId()).orElseThrow();
        assertThat(savedRoute.getStatus()).isEqualTo(RouteStatus.FINISHED);
        assertThat(savedRoute.getFinishedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
    }

    @Test
    void givenCancelledDropNotFinishedRouteWhenFinishFinishedThenFinish() {
        //given
        final Route route = RouteDataGenerator.route(1, company);
        route.setStatus(RouteStatus.ONGOING);
        routeRepository.save(route);
        final Drop drop = DropDataGenerator.drop(1, route, spot);
        drop.setStatus(DropStatus.CANCELLED);
        dropRepository.save(drop);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStatus(DropStatus.FINISHED);
        dropRepository.save(drop2);

        //when
        routeFinishingScheduler.finishFinished();

        //then
        final Route savedRoute = routeRepository.findById(route.getId()).orElseThrow();
        assertThat(savedRoute.getStatus()).isEqualTo(RouteStatus.FINISHED);
        assertThat(savedRoute.getFinishedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
    }

    @Test
    void givenLiveDropNotFinishedRouteWhenFinishFinishedThenDoNothing() {
        //given
        final Route route = RouteDataGenerator.route(1, company);
        route.setStatus(RouteStatus.ONGOING);
        routeRepository.save(route);
        final Drop drop = DropDataGenerator.drop(1, route, spot);
        drop.setStatus(DropStatus.LIVE);
        dropRepository.save(drop);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStatus(DropStatus.FINISHED);
        dropRepository.save(drop2);

        //when
        routeFinishingScheduler.finishFinished();

        //then
        final Route savedRoute = routeRepository.findById(route.getId()).orElseThrow();
        assertThat(savedRoute.getStatus()).isEqualTo(RouteStatus.ONGOING);
        assertThat(savedRoute.getFinishedAt()).isNull();
    }

    @Test
    void givenFinishedDropFinishedRouteWhenFinishFinishedThenDoNothing() {
        //given
        final Route route = RouteDataGenerator.route(1, company);
        route.setStatus(RouteStatus.FINISHED);
        routeRepository.save(route);
        final Drop drop = DropDataGenerator.drop(1, route, spot);
        drop.setStatus(DropStatus.FINISHED);
        dropRepository.save(drop);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStatus(DropStatus.FINISHED);
        dropRepository.save(drop2);

        //when
        routeFinishingScheduler.finishFinished();

        //then
        final Route savedRoute = routeRepository.findById(route.getId()).orElseThrow();
        assertThat(savedRoute.getStatus()).isEqualTo(RouteStatus.FINISHED);
        assertThat(savedRoute.getFinishedAt()).isNull();
    }
}