package io.penguinstats.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import io.penguinstats.constant.Constant.CacheValue;
import io.penguinstats.model.Notice;

public interface NoticeService {

    @Caching(evict = {@CacheEvict(value = CacheValue.LISTS, key = "'noticeList'")})
    public void saveNotice(Notice notice);

    @Cacheable(value = CacheValue.LISTS, key = "'noticeList'")
    public List<Notice> getAllNotice();

}
