package org.example.pluginvorlage.restendpoint;

import de.itc.onkostar.api.IOnkostarApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restendpoint")
public class DemoRestController {

    private final IOnkostarApi onkostarApi;

    public DemoRestController(IOnkostarApi onkostarApi) {
        this.onkostarApi = onkostarApi;
    }

    /**
     * Requests procedure with given ID and returns HTTP 200 - OK with body containing related form name.
     * @param id The requested procedure ID
     * @return HTTP 200 - OK with body containing related form name or HTTP 404 - NOT_FOUND if no such procedure exists.
     */
    @GetMapping("/procedures/{id}/formname")
    public ResponseEntity<String> getFormname(@PathVariable int id) {
        var form = this.onkostarApi.getProcedure(id);

        if (null == form) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(form.getFormName());
    }

}
