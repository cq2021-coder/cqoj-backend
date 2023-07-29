package com.cq.cqoj.aop;

import com.cq.cqoj.annotation.AuthCheck;
import com.cq.cqoj.common.ResultCodeEnum;
import com.cq.cqoj.exception.BusinessException;
import com.cq.cqoj.model.entity.User;
import com.cq.cqoj.model.enums.UserRoleEnum;
import com.cq.cqoj.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 权限校验 AOP
 *
 * @author 程崎
 * @since 2023/07/29
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint 切入点
     * @param authCheck 权限校验
     * @return {@link Object}
     * @throws Throwable throwable
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        UserRoleEnum[] userRoleEnums = authCheck.value();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User loginUser = userService.getLoginUser(request.getSession());
        if (ObjectUtils.isEmpty(loginUser)) {
            throw new BusinessException(ResultCodeEnum.NOT_LOGIN_ERROR);
        }
        UserRoleEnum userRole = loginUser.getUserRole();
        if (userRole.equals(UserRoleEnum.BAN)) {
            throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR);
        }
        if (Arrays.stream(userRoleEnums).noneMatch(userRoleEnum -> userRoleEnum == userRole)) {
            throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR);
        }

        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

