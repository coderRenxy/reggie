package com.hbpu.reggie.controller;

import com.hbpu.reggie.common.BaseContext;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.AddressBook;
import com.hbpu.reggie.service.AddressBookService;
import com.hbpu.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/addressBook")
@RestController
public class AddressBookController {

    @Autowired
    AddressBookService addressBookService;
    @Autowired
    UserService userService;

    @GetMapping("/list")
    public R<List<AddressBook>> addressListApi(){
        return addressBookService.getAddressBookList();
    }

    @PostMapping
    public R<String> addAddressApi(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("ok");
    }

    @PutMapping
    public R<String> updateAddressApi(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("ok");
    }

    @GetMapping("/{id}")
    public R<AddressBook> getAddressDetail(@PathVariable long id){
        return R.success(addressBookService.getById(id));
    }

    @DeleteMapping
    public R<String> deleteAddressApi(Long ids){
        addressBookService.removeById(ids);
        return R.success("ok");
    }

    @PutMapping("/default")
    public R<String> setDefaultAddressApi(@RequestBody AddressBook addressBook){
        return addressBookService.setDefaultAddressApi(addressBook.getId());
    }

    @GetMapping("/default")
    public R<AddressBook> getDefaultAddress(){
        return addressBookService.getDefaultAddress();
    }


}
