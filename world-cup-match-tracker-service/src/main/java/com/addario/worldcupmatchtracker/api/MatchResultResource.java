package com.addario.worldcupmatchtracker.api;

import com.addario.worldcupmatchtracker.dto.MatchResultRequest;
import com.addario.worldcupmatchtracker.dto.MatchResultResponse;
import com.addario.worldcupmatchtracker.service.MatchResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchResultResource {

    private final MatchResultService service;

    @GetMapping
    public Page<MatchResultResponse> search(
            @RequestParam(required = false) String team,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String stage,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.search(team, date, stage, page, size);
    }

    @GetMapping("/{id}")
    public MatchResultResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MatchResultResponse create(@Valid @RequestBody MatchResultRequest request) {
        return service.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
