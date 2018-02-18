package org.businesskeeper.test.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CURRENCY")
public class CurrencyEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String name;
	
	public CurrencyEntity() {
	}

	public CurrencyEntity(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
