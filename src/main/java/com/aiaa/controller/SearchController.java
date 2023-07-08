package com.aiaa.controller;

import com.aiaa.entity.DiscussPost;
import com.aiaa.entity.Page;
import com.aiaa.entity.SearchResult;
import com.aiaa.entity.User;
import com.aiaa.service.DiscussPostService;
import com.aiaa.service.ElasticSearchService;
import com.aiaa.service.LikeService;
import com.aiaa.service.UserService;
import com.aiaa.util.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private DiscussPostService discussPostService;

    // search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        if (keyword.length() == 0 || keyword.isEmpty() || keyword.equals("")){
            page.setRows(discussPostService.findDiscussPostRows(0));
            page.setPath("/index");

            List<DiscussPost> discussPostList = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());

            List<Map<String, Object>> discussPosts = new ArrayList<>();
            if (discussPostList != null) {
                for (DiscussPost post : discussPostList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("post", post);
                    User user = userService.findUserById(post.getUserId());
                    map.put("user", user);
                    discussPosts.add(map);
                }
            }
            model.addAttribute("discussPosts", discussPosts);
            return "/index";
        }
        //搜索帖子
        try {
            SearchResult searchResult = elasticSearchService.searchDiscussPost(keyword, (page.getCurrent() - 1)*10, page.getLimit());
            List<Map<String,Object>> discussPosts = new ArrayList<>();
            List<DiscussPost> list = searchResult.getList();
            if(list != null) {
                for (DiscussPost post : list) {
                    Map<String,Object> map = new HashMap<>();
                    //帖子 和 作者
                    map.put("post",post);
                    map.put("user",userService.findUserById(post.getUserId()));
                    // 点赞数目
                    map.put("likeCount",likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_POST, post.getId()));

                    discussPosts.add(map);
                }
            }
            model.addAttribute("discussPosts",discussPosts);
            model.addAttribute("keyword",keyword);
            //分页信息
            page.setPath("search?keyword=" + keyword);
            page.setRows(searchResult.getTotal() == 0 ? 0 : (int) searchResult.getTotal());
        } catch (IOException e) {
            logger.error("系统出错，没有数据：" + e.getMessage());
        }
        return "site/search";
    }

}
