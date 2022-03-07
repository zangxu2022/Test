package com.example.demo.ao;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TouchUserDataResponse<E> {

    @ApiModelProperty("数据总量")
    private Long total;

    @ApiModelProperty("当前偏移量")
    private Long offset;

    @ApiModelProperty("当前分页值")
    private Long size;

    @ApiModelProperty("实际返回的数据")
    private List<E> list;

    public Boolean success(){
        return !CollectionUtils.isEmpty(list);
    }

    public Boolean isLastPage(){
        return offset.equals(total) || list.size() < this.size;
    }

}
