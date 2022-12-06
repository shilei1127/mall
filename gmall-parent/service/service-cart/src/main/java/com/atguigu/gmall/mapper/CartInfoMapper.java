package com.atguigu.gmall.mapper;

import com.atguigu.gmall.model.cart.CartInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 购物车相关的mapper映射
 */
@Mapper
public interface CartInfoMapper extends BaseMapper<CartInfo> {
    /**
     * 全部选中或者全不选
     * @param username
     * @param status
     */
    @Update("update cart_info set is_checked =#{status}  where user_id=#{username}")
    int checkAll(@Param("username") String username, @Param("status") Short status);

    /**
     * 选中一个或者不选
     * @param username
     * @param status
     * @param id
     */
    @Update("update cart_info set is_checked = #{status} where user_id=#{username} and id = #{id}")
    int checkOne(@Param("username") String username,@Param("status") Short status,@Param("id") Long id);
}
