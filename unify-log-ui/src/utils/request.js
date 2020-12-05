import axios from 'axios';

const service = axios.create({
    // process.env.NODE_ENV === 'development' 来判断是否开发环境
    // easy-mock服务挂了，暂时不使用了
    // baseURL: 'https://www.easy-mock.com/mock/592501a391470c0ac1fab128',
    //baseURL: 'http://localhost:8080/log',
    timeout: 5000
});

service.interceptors.request.use(
    config => {
        var userInfo = sessionStorage.getItem('userInfo');
        if (userInfo && userInfo != "") {
            let user = JSON.parse(userInfo)
            if (user && user.token) {
                config.headers['Authorization'] = 'Bearer ' + user.token
            }
        }   
        return config;
    },
    error => {
        console.log(error);
        return Promise.reject();
    }
);

service.interceptors.response.use(
    response => {
        if (response.status === 200) {
            return response.data;
        } else {
            Promise.reject();
        }
    },
    error => {
        console.log(error);
        return Promise.reject();
    }
);

export default service;
