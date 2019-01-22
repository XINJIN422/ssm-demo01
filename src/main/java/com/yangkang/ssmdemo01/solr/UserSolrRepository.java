package com.yangkang.ssmdemo01.solr;

import com.yangkang.ssmdemo01.mvc.entity.User;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

/**
 * UserSolrRepository
 *
 * @author yangkang
 * @date 2019/1/17
 */
//网上看到都是结合springboot使用, 不知道怎样注入solr host
public interface UserSolrRepository extends SolrCrudRepository<User, String> {
    List<User> findByUsername(String username);
}
