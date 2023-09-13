package com.soogung.simblue.domain.application.presentation;

import com.soogung.simblue.domain.application.presentation.dto.request.ApplicationRequest;
import com.soogung.simblue.domain.application.presentation.dto.request.FilterListRequest;
import com.soogung.simblue.domain.application.presentation.dto.request.FilterRequest;
import com.soogung.simblue.domain.application.presentation.dto.response.ApplicationDetailResponse;
import com.soogung.simblue.domain.application.presentation.dto.response.ApplicationFormResponse;
import com.soogung.simblue.domain.application.presentation.dto.response.ApplicationListResponse;
import com.soogung.simblue.domain.application.presentation.dto.response.ApplicationResponse;
import com.soogung.simblue.domain.application.presentation.dto.response.ApplicationResultResponse;
import com.soogung.simblue.domain.application.presentation.dto.response.ApplicationStatusResponse;
import com.soogung.simblue.domain.application.service.CloseApplicationService;
import com.soogung.simblue.domain.application.service.CreateApplicationService;
import com.soogung.simblue.domain.application.service.DeleteApplicationService;
import com.soogung.simblue.domain.application.service.QueryAlwaysApplicationService;
import com.soogung.simblue.domain.application.service.QueryApplicationDetailService;
import com.soogung.simblue.domain.application.service.QueryApplicationFormService;
import com.soogung.simblue.domain.application.service.QueryApplicationResultService;
import com.soogung.simblue.domain.application.service.QueryDeadlineApplicationService;
import com.soogung.simblue.domain.application.service.QueryLatestApplicationService;
import com.soogung.simblue.domain.application.service.QueryMyApplicationService;
import com.soogung.simblue.domain.application.service.QueryPagingApplication;
import com.soogung.simblue.domain.application.service.SearchApplicationService;
import com.soogung.simblue.domain.application.service.UpdateApplicationService;
import com.soogung.simblue.global.error.exception.ErrorCode;
import com.soogung.simblue.global.error.exception.SimblueException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final CreateApplicationService applicationService;
    private final QueryDeadlineApplicationService queryDeadlineApplicationService;
    private final QueryLatestApplicationService queryLatestApplicationService;
    private final QueryAlwaysApplicationService queryAlwaysApplicationService;
    private final QueryPagingApplication queryPagingApplication;
    private final QueryApplicationDetailService queryApplicationDetailService;
    private final QueryApplicationFormService queryApplicationFormService;
    private final QueryMyApplicationService queryMyApplicationService;
    private final QueryApplicationResultService queryApplicationResultService;
    private final SearchApplicationService searchApplicationService;
    private final UpdateApplicationService updateApplicationService;
    private final DeleteApplicationService deleteApplicationService;
    private final CloseApplicationService closeApplicationService;

    @PostMapping
    public void createApplication(@RequestBody @Valid ApplicationRequest request) {
        applicationService.execute(request);
    }

    @GetMapping
    public List<ApplicationResponse> getApplications(@RequestParam(name = "type", required = true) String applicationType) {
        if (applicationType.equals("deadline")) {
            return queryDeadlineApplicationService.execute();
        } else if (applicationType.equals("latest")) {
            return queryLatestApplicationService.execute();
        } else if (applicationType.equals("always")) {
            return queryAlwaysApplicationService.execute();
        } else {
            throw new SimblueException(ErrorCode.BAD_REQUEST);
        }
    }

    @GetMapping("/paging")
    public ApplicationListResponse getPagingApplication(@PageableDefault(size = 4) Pageable pageable) {
        return queryPagingApplication.execute(pageable);
    }

    @GetMapping("/{id}")
    public ApplicationDetailResponse getApplicationDetail(@PathVariable Long id) {
        return queryApplicationDetailService.execute(id);
    }

    @GetMapping("/{id}/form")
    public ApplicationFormResponse getApplicationForm(@PathVariable Long id) {
        return queryApplicationFormService.execute(id);
    }

    @GetMapping("/my")
    public ApplicationStatusResponse getMyApplication() {
        return queryMyApplicationService.execute();
    }

    @GetMapping("/{id}/result")
    public ApplicationResultResponse getApplicationResult(
            @PathVariable Long id,
            @RequestBody(required = false) @Valid FilterListRequest filterList
    ) {
        return queryApplicationResultService.execute(id, filterList);
    }

    @GetMapping("/search")
    public ApplicationListResponse searchApplication(@RequestParam(name = "q") String q) {
        return searchApplicationService.execute(q);
    }

    @PutMapping("/{id}")
    public void updateApplication(
            @PathVariable Long id,
            @RequestBody @Valid ApplicationRequest request
    ) {
        updateApplicationService.execute(id, request);
    }

    @PutMapping("/{id}/close")
    public void closeApplication(@PathVariable Long id) {
        closeApplicationService.execute(id);
    }

    @DeleteMapping("/{id}")
    public void deleteApplication(@PathVariable Long id) {
        deleteApplicationService.execute(id);
    }
}
