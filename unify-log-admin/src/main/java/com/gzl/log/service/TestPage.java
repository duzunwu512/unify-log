package com.gzl.log.service;

import com.gzl.log.entity.BizLog;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestPage {

    public static void main(String args[]){
        System.out.println("--------------------------------------------------------");

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Map<String, String> map = null;
        for(int i=0; i<26; i++){
            map = new HashMap<String, String>();
            map.put("name", "name["+i+"]");
            map.put("address", "address["+i+"]");

            data.add(map);
        }
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC,"createDate");
        Page<Map<String, String>> page = new PageImpl<Map<String, String>>(data,pageable, 26);

        pageable = PageRequest.of(1, 10, Sort.Direction.DESC,"createDate");
        page = new PageImpl<Map<String, String>>(data,pageable, 26);

        pageable = PageRequest.of(2, 10, Sort.Direction.DESC,"createDate");
        page = new PageImpl<Map<String, String>>(data,pageable, 26);



        System.out.println("-----------------------end---------------------------------");
    }
}
