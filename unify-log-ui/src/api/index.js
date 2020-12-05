import request from '../utils/request';

export const fetchData = query => {
    return request({
        url: './table.json',
        method: 'get',
        params: query
    });
};

//-----------------BizLog----------------------------
export const fetchBizLog = query => {
    return request({
        url: './api/bizlog/page',
        method: 'get',
        params: query
    });
};

export const deleteBizLog = ids => {
    return request({
        url: './api/bizlog/delete/'+ids,
        method: 'post'
    });
};

export const updateBizLog = data => {
    return request({
        url: './api/bizlog/update',
        method: 'post',
        data: data
    });
};

export const findBizLog = id => {
    return request({
        url: './api/bizlog/get/'+id,
        method: 'get'
    });
};



//-----------------------------------------------
export const fetchRunLog = query => {
    return request({
        url: './api/runlog/page',
        method: 'get',
        params: query
    });
};

export const findById = id => {
    return request({
        url: './api/runlog/get/'+id,
        method: 'get',
        params: {}
    });
};

//-----------------------------------------------

export const login = data => {
    return request({
        url: './api/user/login',
        method: 'post',
        data: data,
        timeout: 2000,
    });
};