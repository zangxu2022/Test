package com.example.demo.ao;

public interface TouchUserDataInterface {

    /***
     * 确认数据可读
     * @return
     */
    public Boolean alread();

    /***
     * 读取数据
     * @param offset
     * @param size
     * @return
     */
    public <T> TouchUserDataResponse<T> load(Long offset, Long size, Class<T> classz);

}
