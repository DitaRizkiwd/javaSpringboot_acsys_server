package com.acsys.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//response secara global untuk semua request
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse<T> {
    private T data;

    private T error;

}
