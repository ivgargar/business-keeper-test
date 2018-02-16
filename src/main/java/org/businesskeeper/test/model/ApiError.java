package org.businesskeeper.test.model;

public class ApiError {
	public String status;
	public String title;
	public String description;
	
	public ApiError() {
	}
	
	public ApiError(String status, String title, String description) {
		this.status = status;
		this.title = title;
		this.description = description;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
