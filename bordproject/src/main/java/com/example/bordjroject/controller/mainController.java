package com.example.bordjroject.controller;

import com.example.bordjroject.Service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.bordjroject.Entity.Board_Entity;
import com.example.bordjroject.Service.BoardService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@RequiredArgsConstructor
@Controller
public class mainController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/")
    public String bord(HttpSession session, Model model) {
        List<Board_Entity> posts = boardService.findAllpost();
        model.addAttribute("posts", posts);

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }


        model.addAttribute("username", username);
        return "/bord";
    }


    @GetMapping("/bordwrite")
    public String bordWrite(HttpSession session, Model model) {
    return "/bordwrite";
    }
    @PostMapping("/bordwrite")
    public String bordwrite(Board_Entity board, HttpSession session, @RequestParam("file") MultipartFile file) throws IOException {

        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login";
        }
        board.setUsername(username);

        boardService.write(board, file);
        return "redirect:/";
    }

    @GetMapping("/files/{filename}")
    @ResponseBody
    public void downloadFile(@PathVariable String filename, HttpServletResponse response) throws IOException {
        boardService.downloadFile(filename, response);
    }

    @GetMapping("/mypage")
    public String myPage(HttpSession session ,Model model) {
        String username = (String) session.getAttribute("username");
        if(username == null) return "redirect:/login";

        List<Board_Entity> myPosts = boardService.findPostsByUsername(username);
        model.addAttribute("posts", myPosts);
        model.addAttribute("username", username);
        return "/mypage";
    }

    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable int id, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if(username == null) return "redirect:/login";

        boardService.deletePost(id, username);
        return "redirect:/mypage";
    }

    @GetMapping("/posts/edit/{id}")
    public String editPostForm(@PathVariable Integer id, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if(username == null) return "redirect:/login";

        Board_Entity post = boardService.findPostById(id);
        if (post == null || !post.getUsername().equals(username)) {
            return "redirect:/mypage ";
        }

        model.addAttribute("post", post);
        return "/editpost";
    }
    @PostMapping("/posts/edit/{id}")
    public String editPost(@PathVariable Integer id, HttpSession session, @ModelAttribute Board_Entity updatePost, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        String username = (String) session.getAttribute("username");
        if(username == null) return "redirect:/login";

        boardService.updatePost(id, updatePost, file, username);
        return "redirect:/mypage";
    }


    @GetMapping("/login")
    public String boardWriteForm() {
        return "login";
    }

    private final UserService userService;
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        boolean result = userService.login(username, password);

        if (result) {
            session.setAttribute("username", username);
            return "redirect:/";
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    private final UserService UserService;
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        UserService.registerUser(username,password);
        return "redirect:/";
    }
}