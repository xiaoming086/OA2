package cn.itcast.oa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.oa.base.BaseDaoImpl;
import cn.itcast.oa.domain.Role;
import cn.itcast.oa.service.RoleService;

@Service
@Transactional
public class RoleServiceImpl extends BaseDaoImpl<Role> implements RoleService {

}
