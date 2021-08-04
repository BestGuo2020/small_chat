package top.bestguo.chat.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.bestguo.chat.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Entity top.bestguo.chat.entity.User
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

    /**
     * 查询生成的用户 id 是否存在
     *
     * @param userId 生成的用户id
     * @return 返回是否有存在
     */
    Integer findUserIdByAll(@Param("userId") Integer userId);

}




