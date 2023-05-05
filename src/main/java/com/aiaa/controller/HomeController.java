package com.aiaa.controller;

import com.aiaa.entity.DiscussPost;
import com.aiaa.entity.Page;
import com.aiaa.entity.User;
import com.aiaa.service.DiscussPostService;
import com.aiaa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        // 方法调用钱,SpringMVC会自动实例化Model和Page,并将Page注入Model.
        // 所以,在thymeleaf中可以直接访问Page对象中的数据.
        page.setRows(discussPostService.findDiscussPostRows(166));
        page.setPath("/index");

        System.out.println(page.getRows());
        System.out.println(page.getOffset());
        System.out.println(page.getLimit());

        List<DiscussPost> list = discussPostService.findDiscussPosts(166, page.getOffset(), page.getLimit());
        System.out.println(list.toString());

        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                System.out.println("id:"+post.getUser_id());
                User user = userService.findUserById(post.getUser_id());
                System.out.println("user:"+user);
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index.html";
    }

}
