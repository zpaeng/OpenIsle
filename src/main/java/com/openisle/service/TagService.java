package com.openisle.service;

import com.openisle.model.Tag;
import com.openisle.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Tag createTag(String name, String describe, String icon) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setDescribe(describe);
        tag.setIcon(icon);
        return tagRepository.save(tag);
    }

    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    public Tag getTag(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
    }

    public List<Tag> listTags() {
        return tagRepository.findAll();
    }
}
