package com.soogung.simblue.domain.application.service;

import com.soogung.simblue.domain.application.domain.Application;
import com.soogung.simblue.domain.application.domain.Question;
import com.soogung.simblue.domain.application.domain.Reply;
import com.soogung.simblue.domain.application.domain.ReplyBlock;
import com.soogung.simblue.domain.application.domain.repository.*;
import com.soogung.simblue.domain.application.facade.ApplicationFacade;
import com.soogung.simblue.domain.notice.presentation.dto.response.NoticeResponse;
import com.soogung.simblue.domain.application.presentation.dto.response.ApplicationResponse;
import com.soogung.simblue.domain.application.presentation.dto.response.ApplicationResultBlockResponse;
import com.soogung.simblue.domain.application.presentation.dto.response.ApplicationResultResponse;
import com.soogung.simblue.domain.notice.domain.repository.NoticeRepository;
import com.soogung.simblue.domain.user.domain.Student;
import com.soogung.simblue.domain.user.domain.Teacher;
import com.soogung.simblue.domain.user.exception.AuthorityMismatchException;
import com.soogung.simblue.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueryApplicationResultService {

    private final UserFacade userFacade;
    private final ApplicationFacade applicationFacade;
    private final NoticeRepository noticeRepository;
    private final OwnerRepository ownerRepository;
    private final QuestionRepository questionRepository;
    private final ReplyBlockRepository replyBlockRepository;

    @Transactional(readOnly = true)
    public ApplicationResultBlockResponse execute(Long id) {
        Teacher teacher = userFacade.findTeacherByUser(userFacade.getCurrentUser());
        checkPermission(id, teacher.getId());

        Application application = applicationFacade.findApplicationById(id);

        List<NoticeResponse> noticeList = noticeRepository.findAllByApplicationIdOrderByIsPinnedDesc(id)
                .stream().map(NoticeResponse::of)
                .collect(Collectors.toList());

        List<String> questionList = questionRepository.findByApplicationIdOrderById(id).stream()
                .map(Question::getQuestion)
                .collect(Collectors.toList());

        List<ApplicationResultResponse> resultList = replyBlockRepository
                .findApplicationResult(id).stream()
                .map(this::createApplicationUserResponseResponse)
                .collect(Collectors.toList());


        return new ApplicationResultBlockResponse(
                ApplicationResponse.of(application),
                noticeList,
                questionList,
                resultList
        );
    }

    private void checkPermission(Long applicationId, Long teacherId) {
        if (!ownerRepository.existsByApplicationIdAndTeacherId(applicationId, teacherId)) {
            throw AuthorityMismatchException.EXCEPTION;
        }
    }

    private ApplicationResultResponse createApplicationUserResponseResponse(ReplyBlock block) {
        Student student = block.getStudent();

        List<String> answerList = block.getRequests().stream()
                .collect(Collectors.groupingBy(r -> r.getQuestion().getId(), TreeMap<Long, List<Reply>>::new, Collectors.toList()))
                .values().stream()
                .map(this::getResult)
                .collect(Collectors.toList());

        return new ApplicationResultResponse(
                student.getUser().getName(),
                student.getStudentNumber(),
                answerList
        );
    }

    private String getResult(List<Reply> request) {
        return request.stream()
                .map(Reply::getAnswer)
                .collect(Collectors.joining(", "));
    }
}
