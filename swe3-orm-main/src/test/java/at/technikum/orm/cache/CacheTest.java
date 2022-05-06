package at.technikum.orm.cache;


import org.junit.Assert;
import org.junit.Test;

public class CacheTest {

    @Test
    public void testPutAndGetSingleEntity() {
        TestEntity testEntity = new TestEntity(1);
        Cache cache = new Cache();
        cache.put(testEntity);
        TestEntity retval =  cache.get(TestEntity.class, 1);
        Assert.assertNotNull(retval);
    }

    @Test
    public void testPutAndGetTwoEntities() {
        TestEntity testEntity1 = new TestEntity(1);
        TestEntity testEntity2 = new TestEntity(2);
        Cache cache = new Cache();
        cache.put(testEntity1);
        cache.put(testEntity2);
        TestEntity retval1 =  cache.get(TestEntity.class, 1);
        Assert.assertNotNull(retval1);
        Assert.assertEquals(Integer.valueOf(1), retval1.getId());
        TestEntity retval2 = cache.get(TestEntity.class, 2);
        Assert.assertNotNull(retval2);
        Assert.assertEquals(Integer.valueOf(2), retval2.getId());
    }

    @Test
    public void testPutAndGetDifferentEntitiesWithSameId() {
        TestEntity testEntity1 = new TestEntity(1);
        TestEntity2 testEntity2 = new TestEntity2(1);
        Cache cache = new Cache();
        cache.put(testEntity1);
        cache.put(testEntity2);
        TestEntity retval1 =  cache.get(TestEntity.class, 1);
        Assert.assertNotNull(retval1);
        Assert.assertEquals(Integer.valueOf(1), retval1.getId());
        TestEntity2 retval2 = cache.get(TestEntity2.class, 1);
        Assert.assertNotNull(retval2);
        Assert.assertEquals(Integer.valueOf(1), retval2.getId());
    }
}
