package com.luoboduner.moo.info.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessInfo {
    private int pid;
    private String process_name;
    private String process_execute_path;
}
