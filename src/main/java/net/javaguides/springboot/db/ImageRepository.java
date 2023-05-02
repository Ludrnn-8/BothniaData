package net.javaguides.springboot.db;

import org.springframework.data.jpa.repository.JpaRepository;

import net.javaguides.springboot.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long>{
    
}
