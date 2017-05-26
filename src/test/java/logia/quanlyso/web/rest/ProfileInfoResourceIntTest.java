package logia.quanlyso.web.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.github.jhipster.config.JHipsterProperties;
import logia.quanlyso.QuanlysoApp;

/**
 * Test class for the ProfileInfoResource REST controller.
 *
 * @see ProfileInfoResource
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = QuanlysoApp.class)
public class ProfileInfoResourceIntTest {

    /** The environment. */
    @Mock
    private Environment environment;

    /** The j hipster properties. */
    @Mock
    private JHipsterProperties jHipsterProperties;

    /** The rest profile mock mvc. */
    private MockMvc restProfileMockMvc;

    /**
     * Setup.
     */
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        String mockProfile[] = {"test"};
        JHipsterProperties.Ribbon ribbon = new JHipsterProperties.Ribbon();
        ribbon.setDisplayOnActiveProfiles(mockProfile);
        when(jHipsterProperties.getRibbon()).thenReturn(ribbon);

        String activeProfiles[] = {"test"};
        when(environment.getDefaultProfiles()).thenReturn(activeProfiles);
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);

        ProfileInfoResource profileInfoResource = new ProfileInfoResource(environment, jHipsterProperties);
        this.restProfileMockMvc = MockMvcBuilders
            .standaloneSetup(profileInfoResource)
            .build();
    }

    /**
     * Gets the profile info with ribbon.
     *
     * @return the profile info with ribbon
     * @throws Exception the exception
     */
    @Test
    public void getProfileInfoWithRibbon() throws Exception {
        restProfileMockMvc.perform(get("/api/profile-info"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    /**
     * Gets the profile info without ribbon.
     *
     * @return the profile info without ribbon
     * @throws Exception the exception
     */
    @Test
    public void getProfileInfoWithoutRibbon() throws Exception {
        JHipsterProperties.Ribbon ribbon = new JHipsterProperties.Ribbon();
        ribbon.setDisplayOnActiveProfiles(null);
        when(jHipsterProperties.getRibbon()).thenReturn(ribbon);

        restProfileMockMvc.perform(get("/api/profile-info"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    /**
     * Gets the profile info without active profiles.
     *
     * @return the profile info without active profiles
     * @throws Exception the exception
     */
    @Test
    public void getProfileInfoWithoutActiveProfiles() throws Exception {
        String emptyProfile[] = {};
        when(environment.getDefaultProfiles()).thenReturn(emptyProfile);
        when(environment.getActiveProfiles()).thenReturn(emptyProfile);

        restProfileMockMvc.perform(get("/api/profile-info"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }
}
