package com.cloudcollection.service.impl;

import com.cloudcollection.domain.User;
import com.cloudcollection.domain.view.IndexCollectorView;
import com.cloudcollection.repository.CollectorRepository;
import com.cloudcollection.repository.UserRepository;
import com.cloudcollection.service.CollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 获取收藏家
 * @Auth: yuyang
 * @Date: 2017/1/19 14:14
 * @Version: 1.0
 **/
@Service
public class CollectorServiceImpl implements CollectorService {

    @Autowired
    private CollectorRepository collectorRepository;
    @Autowired
    private UserRepository userRepository;


    /**
     * 获取收藏家
     *
     * @return
     */
    @Override
    public IndexCollectorView getCollectors() {
        IndexCollectorView indexCollectorView = new IndexCollectorView();
        try {
            Long mostCollectUser = collectorRepository.getMostCollectUser();
            indexCollectorView.setMostCollectUser(userRepository.findById(mostCollectUser).orElse(new User()));
            Long mostFollowedUser = collectorRepository.getMostFollowedUser(mostCollectUser);
            indexCollectorView.setMostFollowedUser(userRepository.findById(mostFollowedUser).orElse(new User()));
            String notUserIds = mostCollectUser + "," + mostFollowedUser;
            Long mostPraisedUser = collectorRepository.getMostPraisedUser(notUserIds);
            indexCollectorView.setMostPraisedUser(userRepository.findById(mostPraisedUser).orElse(new User()));
            notUserIds += "," + mostPraisedUser;
            Long mostCommentedUser = collectorRepository.getMostCommentedUser(notUserIds);
            indexCollectorView.setMostCommentedUser(userRepository.findById(mostCommentedUser).orElse(new User()));
            notUserIds += "," + mostCommentedUser;
            Long mostPopularUser = collectorRepository.getMostPopularUser(notUserIds);
            indexCollectorView.setMostPopularUser(userRepository.findById(mostPopularUser).orElse(new User()));
            notUserIds += "," + mostPopularUser;
            Long mostActiveUser = collectorRepository.getMostActiveUser(notUserIds);
            indexCollectorView.setMostActiveUser(userRepository.findById(mostActiveUser).orElse(new User()));
        } catch (Exception e) {
            System.err.println(e);
        }

        return indexCollectorView;
    }
}
