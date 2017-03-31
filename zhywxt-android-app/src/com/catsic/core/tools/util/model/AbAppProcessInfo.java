/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.catsic.core.tools.util.model;

import android.graphics.drawable.Drawable;


// TODO: Auto-generated Javadoc
/**
 * © 2012 amsoft.cn 
 * 名称：AbProcessInfo.java 
 * 描述：进程信息
 * 
 * @author 还如一梦中
 * @version v1.0
 * @date：2013-11-13 上午9:00:52
 */
public class AbAppProcessInfo implements Comparable<AbAppProcessInfo>{
	
	/** The app name. */
	public String appName;

	/** The name of the process that this object is associated with. */
	public String processName;

	/** The pid of this process; 0 if none. */
	public int pid;

	/**
	 * The user id of this process.
	 */
	public int uid;

	/** The icon. */
	public Drawable icon;

	/**  占用的内存. */
	public long memory;
	
	/**  占用的内存. */
	public String cpu;
	
	/**  进程的状态，其中S表示休眠，R表示正在运行，Z表示僵死状态，N表示该进程优先值是负数. */
	public String status;
	
	/**  当前使用的线程数. */
	public String threadsCount;
	
	/**
	 * Instantiates a new ab process info.
	 */
	public AbAppProcessInfo() {
		super();
	}

	/**
	 * Instantiates a new ab process info.
	 *
	 * @param processName the process name
	 * @param pid the pid
	 * @param uid the uid
	 */
	public AbAppProcessInfo(String processName, int pid, int uid) {
		super();
		this.processName = processName;
		this.pid = pid;
		this.uid = uid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AbAppProcessInfo another) {
		if(this.processName.compareTo(another.processName)==0){
			if(this.memory < another.memory){
	    		return 1;
	    	}else if(this.memory == another.memory){
	    		return 0;
	    	}else{
	    		return -1;
	    	}
		}else{
			return this.processName.compareTo(another.processName);
		}
	}

}
