package io.github.owenxh.irina.controller;

import io.github.owenxh.irina.model.*;
import io.github.owenxh.irina.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Config controller
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@RestController
public class ConfigController {

    private final ConfigService service;

    @Autowired
    public ConfigController(ConfigService configService) {
        this.service = configService;
    }

    @PostMapping("config")
    public void save(@RequestBody ConfigRequest req) {
        service.save(req);
    }

    @DeleteMapping("config")
    public void removeConfig(@RequestParam(name = "dataId") String dataId) {
        service.delete(dataId);
    }

    @GetMapping("config")
    public ResponseEntity<Config> getConfig(@RequestParam(name = "dataId") String dataId) {
        Config config = service.get(dataId);
        if (config == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(config);
    }

}
