package com.soogung.simblue.domain.application.service;

import com.soogung.simblue.domain.application.domain.Application;
import com.soogung.simblue.domain.application.domain.repository.OwnerRepository;
import com.soogung.simblue.domain.application.facade.ApplicationFacade;
import com.soogung.simblue.domain.user.domain.User;
import com.soogung.simblue.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteApplicationService {

    private final UserFacade userFacade;
    private final ApplicationFacade applicationFacade;
    private final OwnerRepository ownerRepository;

    @Transactional
    public void execute(Long id) {
        User user = userFacade.getCurrentUser();
        Application application = applicationFacade.findApplicationById(id);
        application.validateStatus();
        application.validatePermission(ownerRepository, user.getId());

        application.delete();
    }
}
