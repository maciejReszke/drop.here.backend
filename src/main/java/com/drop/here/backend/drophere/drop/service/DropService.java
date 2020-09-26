package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.drop.dto.DropResponse;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.route.entity.Route;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DropService {
    private final DropRepository dropRepository;

    // TODO: 26/09/2020 !
    public List<DropResponse> toDropResponses(Route route) {
        return null;
    }
}
