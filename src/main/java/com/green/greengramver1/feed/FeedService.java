package com.green.greengramver1.feed;

import com.green.greengramver1.common.MyFileUtils;
import com.green.greengramver1.feed.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper mapper;
    private final MyFileUtils myFileUtils;

    public FeedPostRes postFeed(List<MultipartFile> pics, FeedPostReq p) {
        int result = mapper.insFeed(p);
        //파일 저장
        //위치 : feed/${feedID}
        String middlePath = String.format("feed/%d", p.getFeedId());

        //폴더 만들기
        myFileUtils.makeFolders(middlePath);
        /*
        for (int i = 0; i < pics.size(); i++) {
            String savedPicName = (myFileUtils.makeRandomFileName(pics.get(i)));
            String filePath = String.format("%s/%s", middlePath, savedPicName);
            try {
                myFileUtils.transferTo(pics.get(i), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
        FeedPicDto feedPicDto = new FeedPicDto();
        //feedPicDto에 feedId값 넣어주세요.
        feedPicDto.setFeedId(p.getFeedId());

        List<String> list = new ArrayList<>();
        for(MultipartFile pic : pics){
            String savedPicName = myFileUtils.makeRandomFileName(pic);
            String filePath = String.format("%s/%s", middlePath, savedPicName);
            try {
                myFileUtils.transferTo(pic, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            feedPicDto.setPic(savedPicName);
            list.add(savedPicName);
            mapper.insFeedPic(feedPicDto);
        }
        FeedPostRes res = new FeedPostRes();
        res.setPics(list);
        res.setFeedId(p.getFeedId());

        return res;
    }

    public List<FeedGetRes> getFeedList(FeedGetReq p) {
        List<FeedGetRes> list = mapper.selFeedList(p);
        //사진 매핑
        for (FeedGetRes res : list) {
            //DB에서 각 피드에 맞는 사진 정보를 가져온다.
            List<String> picList = mapper.selFeedPicList(res.getFeedId());
            res.setPics(picList);
        }
        return list;
    }
}
