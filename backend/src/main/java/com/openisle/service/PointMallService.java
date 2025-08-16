package com.openisle.service;

import com.openisle.exception.FieldException;
import com.openisle.exception.NotFoundException;
import com.openisle.model.PointGood;
import com.openisle.model.User;
import com.openisle.repository.PointGoodRepository;
import com.openisle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** Service for point mall operations. */
@Service
@RequiredArgsConstructor
public class PointMallService {
    private final PointGoodRepository pointGoodRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public List<PointGood> listGoods() {
        return pointGoodRepository.findAll();
    }

    public int redeem(User user, Long goodId, String contact) {
        PointGood good = pointGoodRepository.findById(goodId)
                .orElseThrow(() -> new NotFoundException("Good not found"));
        if (user.getPoint() < good.getCost()) {
            throw new FieldException("point", "Insufficient points");
        }
        user.setPoint(user.getPoint() - good.getCost());
        userRepository.save(user);
        notificationService.createActivityRedeemNotifications(user, good.getName() + ": " + contact);
        return user.getPoint();
    }
}
