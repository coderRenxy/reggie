package com.hbpu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.AddressBook;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    R<List<AddressBook>> getAddressBookList();

    R<String> setDefaultAddressApi(long id);

    R<AddressBook> getDefaultAddress();
}
