package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.File;
import com.example.demo.entities.User;


@Repository
public interface FileRepository extends JpaRepository<File,Integer> {
	List<File> findByUser(User user);
}