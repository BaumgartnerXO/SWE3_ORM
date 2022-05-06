package at.technikum.orm.model;

import at.technikum.orm.model.domain.BigEntity;
import at.technikum.orm.model.domain.MediumEntity;
import at.technikum.orm.model.domain.SimpleEntity;
import org.junit.Test;

import static org.junit.Assert.*;

public class EntityTest {

    @Test
    public void testParseSimpleEntity() {
        Entity entity = Entity.ofClass(SimpleEntity.class);
        assertNotNull(entity);
        assertNotNull(entity.getPrimaryKey());
        assertTrue(entity.getPrimaryKey().isPK());
        assertEquals("table", entity.getTableName());
        assertEquals(SimpleEntity.class, entity.getType());
        assertEquals(0,entity.getForeignKeys().size());
        assertEquals(1,entity.getEntityFields().size());
    }

    @Test
    public void testParseMediumEntity() {
        Entity entity = Entity.ofClass(MediumEntity.class);
        assertNotNull(entity);
        assertNotNull(entity.getPrimaryKey());
        assertTrue(entity.getPrimaryKey().isPK());
        assertEquals("table", entity.getTableName());
        assertEquals(MediumEntity.class, entity.getType());
        assertEquals(1,entity.getForeignKeys().size());
        assertEquals(4,entity.getEntityFields().size());
        assertEquals(SimpleEntity.class, entity.getForeignKeys().get(0).getType());
    }

    @Test
    public void testParseBigEntity() {
        Entity entity = Entity.ofClass(BigEntity.class);
        assertNotNull(entity);
        assertNotNull(entity.getPrimaryKey());
        assertTrue(entity.getPrimaryKey().isPK());
        assertEquals("table", entity.getTableName());
        assertEquals(BigEntity.class, entity.getType());
        assertEquals(2,entity.getForeignKeys().size());
        assertEquals(6,entity.getEntityFields().size());
        assertEquals(SimpleEntity.class, entity.getForeignKeys().get(0).getType());
        assertEquals(SimpleEntity.class, entity.getForeignKeys().get(1).getType());
    }

}
