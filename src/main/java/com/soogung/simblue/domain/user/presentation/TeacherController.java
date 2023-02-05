package com.soogung.simblue.domain.user.presentation;

import com.soogung.simblue.domain.user.presentation.dto.request.TeacherRequest;
import com.soogung.simblue.domain.user.presentation.dto.request.UpdateTeacherRequest;
import com.soogung.simblue.domain.user.service.JoinTeacherService;
import com.soogung.simblue.domain.user.service.UpdateTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final JoinTeacherService joinTeacherService;
    private final UpdateTeacherService updateTeacherService;

    @PostMapping("/teacher")
    public void joinTeacher(@RequestBody @Valid TeacherRequest request) {
        joinTeacherService.execute(request);
    }

    @PutMapping("/teacher")
    public void updateTeacher(@RequestBody @Valid UpdateTeacherRequest request) {
        updateTeacherService.execute(request);
    }
}
