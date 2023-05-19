package io.github.owenxh.irina.server.controller;

import io.github.owenxh.irina.model.Config;
import io.github.owenxh.irina.model.Type;
import io.github.owenxh.irina.server.service.ConfigService;
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

    @PostMapping("/config")
    public void save(@RequestBody Config config) {
        service.save(config);
    }

    @DeleteMapping("/config")
    public void removeConfig(@RequestParam(name = "dataId") String dataId) {
        service.delete(dataId);
    }

    @GetMapping("/config")
    public ResponseEntity<Config> getConfig(@RequestParam(name = "dataId") String dataId,
                                            @RequestParam(name = "type", required = false) Type type) {
        Config config = service.get(dataId, type);
        if (config == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(config);
    }

}
