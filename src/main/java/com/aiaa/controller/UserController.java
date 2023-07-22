package com.aiaa.controller;

import com.aiaa.annotation.LoginRequired;
import com.aiaa.entity.Comment;
import com.aiaa.entity.DiscussPost;
import com.aiaa.entity.Page;
import com.aiaa.entity.User;
import com.aiaa.service.*;
import com.aiaa.util.CommunityConstant;
import com.aiaa.util.CommunityUtil;
import com.aiaa.util.HostHolder;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aiaa.util.CommunityConstant.ENTITY_TYPE_USER;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${Community.path.upload}")
    private String uploadPath;

    @Value("${Community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

//    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    //上传头像
//    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        model.addAttribute("fileName", fileName);

        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }

        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        // 更新当前用户的头像的路径(web访问路径)
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    //服务器路径显示头像
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }


    //修改密码
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, Model model) {
        if (StringUtils.isBlank(newPassword) || StringUtils.isBlank(oldPassword)) {
            model.addAttribute("oldPasswordMsg", "密码不能为空");
            return "site/setting";
        }
        if (oldPassword.equals(newPassword)) {
            model.addAttribute("oldPasswordMsg", "新旧密码不能一致");
            return "site/setting";
        }
        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!oldPassword.equals(user.getPassword())) {
            model.addAttribute("oldPasswordMsg", "密码错误");
            return "site/setting";
        }
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());

        userService.updatePassword(user.getId(), newPassword);

        user.setPassword(newPassword);

        hostHolder.setUser(user);

        return "redirect:/index";
    }

    // 个人主页
//    @LoginRequired
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }

        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

    // 我的帖子
    @RequestMapping(path = "/profile/myPost/{userId}", method = RequestMethod.GET)
    public String getProfilePost(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        // 用户
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setRows(discussPostService.findDiscussPostRows(userId));
        page.setPath("/user/profile/myPost/" + userId);

        //帖子数
        int row = discussPostService.findDiscussPostRows(userId);
        model.addAttribute("myRow",row);

        List<DiscussPost> discussPostList = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit());

        List<Map<String, Object>> myDiscussPosts = new ArrayList<>();

        if (discussPostList != null){
            for (DiscussPost post : discussPostList) {
                Map<String, Object> map = new HashMap<>();
                map.put("post",post);

                long likeCount = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);

                myDiscussPosts.add(map);
            }
        }
        model.addAttribute("myDiscussPosts", myDiscussPosts);

        return "/site/my-post";
    }

    // 我的评论
    @RequestMapping(path = "/profile/myReply/{userId}", method = RequestMethod.GET)
    public String getProfileReply(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        // 用户
        model.addAttribute("user", user);

        page.setLimit(10);
        page.setRows(commentService.findCommentCount(userId));
        page.setPath("/user/profile/myReply/" + userId);

        //评论数
        int row = commentService.findCommentCount(userId);
        model.addAttribute("replyPostRow",row);

        List<Comment> commentList = commentService.findCommentsByUserId(CommunityConstant.ENTITY_TYPE_POST, userId, page.getOffset(), page.getLimit());

        List<Map<String, Object>> myComments = new ArrayList<>();

        if (commentList != null){
            for (Comment comment: commentList ) {
                Map<String, Object> map = new HashMap<>();
                map.put("comment",comment);

                map.put("comment_post",discussPostService.findDiscussPostByCommentEntityId(comment.getEntityId()));

                myComments.add(map);
            }
        }

        model.addAttribute("myComments", myComments);

        return "/site/my-reply";
    }

}