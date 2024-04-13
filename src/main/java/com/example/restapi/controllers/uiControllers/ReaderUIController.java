package com.example.restapi.controllers.uiControllers;

import com.example.restapi.models.appEntities.BookEntity;
import com.example.restapi.models.appEntities.IssueEntity;
import com.example.restapi.models.appEntities.ReaderEntity;
import com.example.restapi.services.issue.IssueService;
import com.example.restapi.services.reader.ReaderService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ui/reader")
public class ReaderUIController {
    private final ReaderService readerService;
    private final IssueService issueService;

    public ReaderUIController(ReaderService readerService, IssueService issueService) {
        this.readerService = readerService;
        this.issueService = issueService;
    }

    @GetMapping
    public String getReaders(Model model){
        List<ReaderEntity> readers = readerService.findAll();
        model.addAttribute("readers", readers);
        return "readers";
    }

    @GetMapping("/{id}")
    public String getReaderBooks(@PathVariable("id") long id, Model model){
        ReaderEntity reader = readerService.findById(id);
        if (reader == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти пользователя с id=" + id);
        }
        List<BookEntity> books = issueService.getReaderIssues(id).stream()
                .map(IssueEntity::getBook)
                .collect(Collectors.toList());
        model.addAttribute("reader", reader);
        model.addAttribute("books", books);
        return "readerBooks";
    }
}
