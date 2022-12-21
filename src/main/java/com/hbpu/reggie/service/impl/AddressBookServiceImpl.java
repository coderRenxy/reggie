package com.hbpu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hbpu.reggie.common.BaseContext;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.AddressBook;
import com.hbpu.reggie.entity.User;
import com.hbpu.reggie.mapper.AddressBookMapper;
import com.hbpu.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Autowired
    AddressBookMapper addressBookMapper;

    public R<List<AddressBook>> getAddressBookList(){
        LambdaQueryWrapper<AddressBook> lwq = new LambdaQueryWrapper<>();
        lwq.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        List<AddressBook> list = addressBookMapper.selectList(lwq);
        return R.success(list);
    }

    @Transactional
    public R<String> setDefaultAddressApi(long id){
        //构建条件查询器并查询该用户默认地址
        LambdaQueryWrapper<AddressBook> lwq = new LambdaQueryWrapper<>();
        lwq.eq(AddressBook::getIsDefault,1);
        lwq.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        AddressBook addressBook = addressBookMapper.selectOne(lwq);
        //该用户默认地址设置为0，并将当前地址设置为默认的地址
        addressBook.setIsDefault(0);
        addressBookMapper.updateById(addressBook);
        AddressBook addressBook1 = addressBookMapper.selectById(id);
        addressBook1.setIsDefault(1);
        addressBookMapper.updateById(addressBook1);
        return R.success("ok");
    }

    public R<AddressBook> getDefaultAddress(){
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getIsDefault,1);
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        AddressBook addressBook = addressBookMapper.selectOne(lqw);
        if(addressBook != null) return R.success(addressBook);
        else return R.error("fancuole");
    }


}
