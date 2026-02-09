package com.server.service;

import com.server.dao.PrayerRequestDao;
import com.server.dao.UserDao;
import com.server.model.PrayerRequest;
import com.server.model.PrayerRequestDto;
import com.server.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;
import java.util.List;

@Service
@Transactional
public class RestPrayerRequestService implements PrayerRequestService{

    private final PrayerRequestDao prayerRequestDao;
    private final UserDao userDao;
    private final com.server.dao.BadgeDao badgeDao;

    public RestPrayerRequestService (PrayerRequestDao prayerRequestDao, UserDao userDao, com.server.dao.BadgeDao badgeDao){
        this.prayerRequestDao = prayerRequestDao;
        this.userDao = userDao;
        this.badgeDao = badgeDao;
    }

    private User getUser(Principal principal){
        return userDao.getUserByEmail(principal.getName());
    }

    private boolean isAdmin(User user){
        return user.getRole().equals("ROLE_ADMIN");
    }

    @Override
    public List<PrayerRequestDto> getAllPrayerRequestDto(Principal principal, String category, Boolean isAnswered) {
        return prayerRequestDao.getAllPrayerRequest(category, isAnswered);
    }

    @Override
    public PrayerRequest getPrayerRequestById(int id) {
        return prayerRequestDao.getPrayerRequestById(id);
    }

    @Override
    public PrayerRequest createPrayerRequest(PrayerRequest newPrayerRequest, Principal principal) {
        User user = null;
        if (principal != null) {
            user = getUser(principal);
            newPrayerRequest.setRequesterId(user.getId());
        } else {
            // CRITICAL: Ensure anonymous requests don't spoof an ID
            newPrayerRequest.setRequesterId(null);
        }
        PrayerRequest created = prayerRequestDao.createPrayerRequest(newPrayerRequest);

        if (user != null) {
            // Badges
            int count = prayerRequestDao.getAllPrayerRequestByRequesterId(user.getId()).size();
            
            if (count == 1) badgeDao.awardBadge(user.getId(), "1_request");
            if (count == 5) badgeDao.awardBadge(user.getId(), "5_requests");
            if (count == 10) badgeDao.awardBadge(user.getId(), "10_requests");
            if (count == 25) badgeDao.awardBadge(user.getId(), "25_requests");
            if (count == 50) badgeDao.awardBadge(user.getId(), "50_requests");
            if (count == 100) badgeDao.awardBadge(user.getId(), "100_requests");
        }
        return created;
    }

    @Override
    public PrayerRequest updatePrayerRequest(PrayerRequest prayerRequest, Principal principal) {
        PrayerRequest existing = prayerRequestDao.getPrayerRequestById(prayerRequest.getId());
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prayer Request not found");
        }

        User user = getUser(principal);
        if (!isAdmin(user) && existing.getRequesterId() != user.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to update this prayer request");
        }

        // Update allowed fields
        existing.setName(prayerRequest.getName());
        existing.setContent(prayerRequest.getContent());
        existing.setCategory(prayerRequest.getCategory());
        existing.setVisible(prayerRequest.getVisible());
        existing.setAnswered(prayerRequest.getAnswered());
        existing.setAnswerContent(prayerRequest.getAnswerContent());

        return prayerRequestDao.updatePrayerRequest(existing);
    }

    @Override
    public int deletePrayerRequest(int id, Principal principal) {

        PrayerRequest prayerRequest = getPrayerRequestById(id);

        User user = getUser(principal);
        if (!isAdmin(user) && prayerRequest.getRequesterId() != user.getId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this prayer request");
        }

        return prayerRequestDao.deletePrayerRequest(id);
    }
}
