package com.gzl.log.util;

import java.io.Serializable;
import java.util.List;

public class PageInfo<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -776693324591594924L;
    private Integer pageNo = 1;  //当前页码,默认第一页
    private Integer pageSize = 10;  //一页多少行，默认一页10行
    //	private Integer totalPages;  //总页数
    private Long totalElements=0l;  //总记录数
    private List<T> content;

    public PageInfo() {}

    public PageInfo(Integer pageNo, Integer pageSize, Long totalElements, List<T> content){
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.content = content;
    }

    public Integer getPageNo() {
        return pageNo;
    }
    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
    public Integer getPageSize() {
        return pageSize;
    }
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    public Integer getTotalPages() {
        return pageSize == 0 ? 1 : (int) Math.ceil((double) totalElements / (double) pageSize);
    }

    public Long getTotalElements() {
        return totalElements;
    }
    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }
    public List<T> getContent() {
        return content;
    }
    public void setContent(List<T> content) {
        this.content = content;
    }

}
