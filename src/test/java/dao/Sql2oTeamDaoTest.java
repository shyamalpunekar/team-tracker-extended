package dao;

import models.Member;
import models.Team;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by spunek on 8/18/17.
 */
public class Sql2oTeamDaoTest {
    private Sql2oTeamDao teamDao;
    private Connection conn;
    private Sql2oMemberDao memberDao;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        teamDao = new Sql2oTeamDao(sql2o);
        memberDao = new Sql2oMemberDao(sql2o);
        conn = sql2o.open();
    }


    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    public Team setupNewTeam() { return new Team("ROCK");}
    public Team setupNewTeamTwo() { return new Team("IT-RAW");}

    @Test
    public void addingTeamSetsId() throws Exception {
        Team team = setupNewTeam();
        int originalCuisineId = team.getId();
        teamDao.add(team);
        assertNotEquals(originalCuisineId, team.getId()); //how does this work?
    }

    @Test
    public void existingTeamCanBeFoundById() throws Exception {
        Team team = setupNewTeam();
        teamDao.add(team);
        Team foundTeam = teamDao.findById(team.getId());
        assertEquals(team, foundTeam);
    }

    @Test
    public void addedTeamsAreReturnedFromGetAll() throws Exception {
        Team cuisine = setupNewTeam();
        teamDao.add(cuisine);
        assertEquals(1, teamDao.getAll().size());
    }

    @Test
    public void noTeamsReturnsEmptyList() throws Exception {
        assertEquals(0, teamDao.getAll().size());
    }

    @Test
    public void updateChangesTeamContent() throws Exception {
        String initialTeamName = "American";
        Team team = new Team(initialTeamName);
        teamDao.add(team);

        teamDao.update(team.getId(), "American-Warrier");
        Team updatedCuisine = teamDao.findById(team.getId());
        assertNotEquals(initialTeamName, updatedCuisine.getTeamName());
    }

    @Test
    public void deleteByIdDeletesCorrectCuisine() throws Exception {
        Team team = setupNewTeam();
        teamDao.add(team);
        teamDao.deleteTeamById(team.getId());
        assertEquals(0, teamDao.getAll().size());
    }

    @Test
    public void clearAllClearsAll() throws Exception {
        Team team = setupNewTeam();
        Team teamTwo = setupNewTeamTwo();
        teamDao.add(team);
        teamDao.add(teamTwo);
        int daoSize = teamDao.getAll().size();
        teamDao.clearAllTeams();
        assertTrue(daoSize > 0 && daoSize > teamDao.getAll().size());
    }

    @Test
    public void getAllMembersByTeamReturnsMembersCorrectly() throws Exception {
        Team team = new Team ("American");
        teamDao.add(team);
        int teamId = team.getId();
        Member newMember = new Member("American-Warrier", teamId);
        Member newMemberTwo = new Member("Management-Team", teamId);
        Member newMemberThree = new Member("Sports-Team", teamId);
        memberDao.add(newMember);
        memberDao.add(newMemberTwo);


        assertTrue(teamDao.getAllMembersByTeam(teamId).size() == 2);
        assertTrue(teamDao.getAllMembersByTeam(teamId).contains(newMember));
        assertTrue(teamDao.getAllMembersByTeam(teamId).contains(newMemberTwo));
        assertFalse(teamDao.getAllMembersByTeam(teamId).contains(newMemberThree));
    }




}