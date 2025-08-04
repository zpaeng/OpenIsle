package com.openisle.controller;

import com.openisle.dto.DraftDto;
import com.openisle.dto.DraftRequest;
import com.openisle.mapper.DraftMapper;
import com.openisle.model.Draft;
import com.openisle.service.DraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drafts")
@RequiredArgsConstructor
public class DraftController {
    private final DraftService draftService;
    private final DraftMapper draftMapper;

    @PostMapping
    public ResponseEntity<DraftDto> saveDraft(@RequestBody DraftRequest req, Authentication auth) {
        Draft draft = draftService.saveDraft(auth.getName(), req.getCategoryId(), req.getTitle(), req.getContent(), req.getTagIds());
        return ResponseEntity.ok(draftMapper.toDto(draft));
    }

    @GetMapping("/me")
    public ResponseEntity<DraftDto> getMyDraft(Authentication auth) {
        return draftService.getDraft(auth.getName())
                .map(d -> ResponseEntity.ok(draftMapper.toDto(d)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMyDraft(Authentication auth) {
        draftService.deleteDraft(auth.getName());
        return ResponseEntity.ok().build();
    }
}
