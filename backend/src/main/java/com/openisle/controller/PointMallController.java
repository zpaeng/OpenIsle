package com.openisle.controller;

import com.openisle.dto.PointGoodDto;
import com.openisle.dto.PointRedeemRequest;
import com.openisle.mapper.PointGoodMapper;
import com.openisle.model.User;
import com.openisle.service.PointMallService;
import com.openisle.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** REST controller for point mall. */
@RestController
@RequestMapping("/api/point-goods")
@RequiredArgsConstructor
public class PointMallController {
    private final PointMallService pointMallService;
    private final UserService userService;
    private final PointGoodMapper pointGoodMapper;

    @GetMapping
    public List<PointGoodDto> list() {
        return pointMallService.listGoods().stream()
                .map(pointGoodMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/redeem")
    public Map<String, Integer> redeem(@RequestBody PointRedeemRequest req, Authentication auth) {
        User user = userService.findByIdentifier(auth.getName()).orElseThrow();
        int point = pointMallService.redeem(user, req.getGoodId(), req.getContact());
        return Map.of("point", point);
    }
}
