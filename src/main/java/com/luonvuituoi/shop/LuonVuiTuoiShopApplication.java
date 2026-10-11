package com.luonvuituoi.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LuonVuiTuoiShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuonVuiTuoiShopApplication.class, args);
		System.out.println("==================================================");
		System.out.println("SHOP THOI TRANG LUON VUI TUOI DA KHOI CHAY!");
		System.out.println("Trang chu khach hang: http://localhost:8080/");
		System.out.println("Trang quan tri Admin: http://localhost:8080/admin");
		System.out.println("==================================================");
	}

}
