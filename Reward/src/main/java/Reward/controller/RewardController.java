package Reward.controller;

import Reward.domain.User;
import Reward.service.IRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RewardController {
    @Autowired
    IRewardService rewardService;

    @RequestMapping("/calculateRewards/{user}")
    public void calculateRewards(@PathVariable("user") User user) {
        rewardService.calculateRewards(user);
    }
}
