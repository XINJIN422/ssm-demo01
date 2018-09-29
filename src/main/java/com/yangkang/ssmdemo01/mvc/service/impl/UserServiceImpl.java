package com.yangkang.ssmdemo01.mvc.service.impl;

import com.yangkang.ssmdemo01.mvc.dao.Dao;
import com.yangkang.ssmdemo01.mvc.entity.User;
import com.yangkang.ssmdemo01.mvc.entity.User2;
import com.yangkang.ssmdemo01.mvc.service.IUserService;
import com.yangkang.ssmdemo01.tools.SpringContextsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service("userService")
//@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceImpl implements IUserService {

    private static Logger logger = LoggerFactory.getLogger("UserServiceImpl.class");
//    @Resource
//    private IUserDao userDao;

    @Resource(name = "daoSupport")
    private Dao dao;

//    @Resource(name = "transactionManager5")
//    private DataSourceTransactionManager transactionManager;

//    public User selectUser(long userId) {
//        return this.userDao.selectUser(userId);
//    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
    @Cacheable(value = "users",key = "#userId")
    public User selectUser2(String userId) throws Exception {
//        ((IUserService)AopContext.currentProxy()).selectUser5("param1","param2");
        logger.debug("============selectUser2=============");
        return (User)dao.findForObject("UserSQL.selectUserById",userId);
    }

    @Override
    @CacheEvict(value = "users",key = "#noUse")
    public void selectUser4(String noUse) throws Exception {
        logger.debug("============selectUser4=============");
        int tmp = 1/0;  //测试事务回滚时,是否缓存也回滚(实验证明,缓存确实会回滚)
    }

    @Override
    public void selectUser5(String noUse, String noUse2) throws Exception {
        logger.debug("============selectUser5=============");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @CacheEvict(value = "users", key = "#userId")
    public int updateUser(String userId, String userName) throws Exception {
        Map params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("userName",userName);
        return (int)dao.update("UserSQL.updateUsernameById",params);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @CacheEvict(value = "users", key = "#userId")
    public int updateUser2(String userId, String userName) throws Exception {
        Map params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("userName",userName);
//        return (int)dao.update("UserSQL.updateUsernameById2",params);
        int result = (int)dao.update("UserSQL.updateUsernameById2",params);
        int excep = 1/0;
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int testInsertBatch(List<User2> userList) throws Exception {
        long millis = new Date().getTime();
        //第一种插入方法
//        logger.debug("-----------批量插入测试START!-----------");
//        int flag = 0;
//        for (User2 user2 : userList)
//            flag += (int)dao.saveBean(user2);
//        logger.debug("-----------批量插入测试END!-----------用时:" + (new Date().getTime() - millis) + "ms");

        //第二种插入方法
//        logger.debug("-----------批量插入测试START!-----------");
//        int flag = (int)dao.saveBeans(userList);
//        logger.debug("-----------批量插入测试END!-----------用时:" + (new Date().getTime() - millis) + "ms");

        //第三种插入方法
        logger.debug("-----------批量插入测试START!-----------");
        int flag = (int)dao.saveBeans2(userList);
        logger.debug("-----------批量插入测试END!-----------用时:" + (new Date().getTime() - millis) + "ms");

        return flag;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Throwable.class)
    public int testInsertBatch2(List<User2> userList) throws Exception {
        //第四种插入方法(多线程事务控制有问题)
        logger.debug("-----------批量插入测试START!-----------");
        long millis = new Date().getTime();
        List<Thread> threadList = new LinkedList<>();
        //原子变量,保证多线程操作安全
        AtomicInteger atomicSum = new AtomicInteger(0);
        AtomicBoolean atomicNeedrollback = new AtomicBoolean(false);
        //局部内部类
        class SaveBatchThread extends Thread {

            private List<User2> separateUserList;

            SaveBatchThread(List<User2> tmpUserList){
                separateUserList = tmpUserList;
            }

            @Override
            public void run() {
                //不需要这
//                Dao dao2 = (Dao) SpringContextsUtil.getBean("daoSupport", Dao.class);
                try {
                    atomicSum.addAndGet((int) dao.saveBeans(separateUserList));
                } catch (Exception e) {
                    e.printStackTrace();
                    atomicNeedrollback.set(true);
                }
            }
        };
        int cursor = 0;
        while (cursor + 1000 <= userList.size())
            threadList.add(new SaveBatchThread(userList.subList(cursor,cursor += 1000)));
        threadList.add(new SaveBatchThread(userList.subList(cursor,userList.size())));
        for (Thread oneThread : threadList)
            oneThread.start();
        for (Thread oneThread : threadList)
            oneThread.join();
        if (atomicNeedrollback.get() == true)
            throw new Exception("多线程插入失败!");    //虽然设置了事务回滚注解,这边也没用,如果主键在Java就生成了,可以在这根据主键删除已插入的项;也可以扩表新增一个批次号字段,根据插入批次号来删除;
        logger.debug("-----------批量插入测试END!-----------用时:" + (new Date().getTime() - millis) + "ms");
        return atomicSum.intValue();
    }

    @Override
    public int testInsertBatch2TransactionEnhanced(List<User2> userList) throws Exception {
        //第四种插入方法的事务增强版_synchronized+while轮询实现
        //预提交与正式提交分开,当线程数大于数据源连接数时,会报错,因为逻辑是主线程必须等所有分线程的都预提交成功了或者出现预提交失败才释放锁,而分线程又必须等待主线程释放锁后才能正式提交或回滚,
        //      而如果分线程不正式提交或回滚的话,就最多只有最大数据源连接数个线程可以预提交并一直占用这些连接且非空闲状态,其他分线程是不能得到数据源连接的,直到占用的连接超时报错;
        //      因此优化的方向应该是线程数最大只能取最大连接数,然后结合第二第三种方式,分批次提交;
        //这边还有个小问题,因为预提交与正式提交之间有个时间段,所以如果前者成功,mysql服务器瞬间阻塞,后者失败了,那还得手动把已经正式提交的线程插入的数据给删除掉
        logger.debug("-----------批量插入测试START!-----------");
        long millis = new Date().getTime();
        List<Thread> threadList = new LinkedList<>();
        //原子变量,保证多线程操作安全
        AtomicInteger atomicSum = new AtomicInteger(0);
        AtomicBoolean atomicNeedrollback = new AtomicBoolean(false);
        AtomicBoolean commitOrRollback = new AtomicBoolean(true);
        //线程安全的并行集,也可以用CopyOnWriteArrayList
//        Queue<TransactionStatus> statusQueue = new ConcurrentLinkedQueue<>();
        //在方法中调用spring的编程式事务管理
        PlatformTransactionManager transactionManager5 = (PlatformTransactionManager) SpringContextsUtil.getBean("transactionManager5", PlatformTransactionManager.class);
        //局部内部类
        class SaveBatchThread extends Thread {

            private List<User2> separateUserList;

            SaveBatchThread(List<User2> tmpUserList){
                separateUserList = tmpUserList;
            }

            @Override
            public void run() {
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus transactionStatus = transactionManager5.getTransaction(def);
//                statusQueue.add(transactionStatus);
                try {
                    atomicSum.addAndGet((int) dao.saveBeans(separateUserList));
                    //这边把提交或回滚放在同步块之外是因为分线程与主线程一一互斥,但各个分线程之间不用互斥
                    synchronized (commitOrRollback){
                    }
//                    System.out.println("=========阻塞结束=========");
                    if (atomicNeedrollback.get())
                        transactionManager5.rollback(transactionStatus);
                    else
                        transactionManager5.commit(transactionStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                    atomicNeedrollback.set(true);
                    transactionManager5.rollback(transactionStatus);
                }
            }
        };
        int cursor = 0;
        while (cursor + 1000 <= userList.size())
            threadList.add(new SaveBatchThread(userList.subList(cursor,cursor += 1000)));
        threadList.add(new SaveBatchThread(userList.subList(cursor,userList.size())));

        synchronized (commitOrRollback){
            for (Thread oneThread : threadList)
                oneThread.start();
            while (atomicSum.intValue() < userList.size()){
                if (atomicNeedrollback.get())
                    break;
            }
            //实验证明synchronized方法块为空依然会被阻塞
//            Thread.sleep(5000);
//            System.out.println("==========准备结束阻塞===========");
        }

        for (Thread oneThread : threadList)
            oneThread.join();

        //本来想用并行集保存各线程事务状态,然后在主线程里统一提交或回滚,结果不行
//        if (atomicNeedrollback.get() == true){
//            for (TransactionStatus status : statusQueue)
//                transactionManager5.rollback(status);
//        }else {
//            for (TransactionStatus status : statusQueue)
//                transactionManager5.commit(status);
//        }

        logger.debug("-----------批量插入测试END!-----------用时:" + (new Date().getTime() - millis) + "ms");
        return atomicSum.intValue();
    }

    @Override
    public int testInsertBatch2TransactionEnhanced2(List<User2> userList) throws Exception {
        //第四种插入方法的事务增强版_synchronized+wait+notify实现(wait与notify必须先获得锁,即在synchronized之间)
        //预提交与正式提交分开,当线程数大于数据源连接数时,会报错,因为逻辑是主线程必须等所有分线程的都预提交成功了或者出现预提交失败才释放锁,而分线程又必须等待主线程释放锁后才能正式提交或回滚,
        //      而如果分线程不正式提交或回滚的话,就最多只有最大数据源连接数个线程可以预提交并一直占用这些连接且非空闲状态,其他分线程是不能得到数据源连接的,直到占用的连接超时报错;
        //      因此优化的方向应该是线程数最大只能取最大连接数,然后结合第二第三种方式,分批次提交;
        //这边还有个小问题,因为预提交与正式提交之间有个时间段,所以如果前者成功,mysql服务器瞬间阻塞,后者失败了,那还得手动把已经正式提交的线程插入的数据给删除掉
        logger.debug("-----------批量插入测试START!-----------");
        long millis = new Date().getTime();
        List<Thread> threadList = new LinkedList<>();
        //原子变量,保证多线程操作安全
        AtomicInteger atomicSum = new AtomicInteger(0);
        AtomicBoolean atomicNeedrollback = new AtomicBoolean(false);
        AtomicBoolean waitForBranch = new AtomicBoolean(true);
        AtomicBoolean waitForMain = new AtomicBoolean(true);
        //在方法中调用spring的编程式事务管理
        PlatformTransactionManager transactionManager5 = (PlatformTransactionManager) SpringContextsUtil.getBean("transactionManager5", PlatformTransactionManager.class);
        //局部内部类
        class SaveBatchThread extends Thread {

            private List<User2> separateUserList;

            SaveBatchThread(List<User2> tmpUserList){
                separateUserList = tmpUserList;
            }

            @Override
            public void run() {
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus transactionStatus = transactionManager5.getTransaction(def);
                try {
                    int tmpResult = atomicSum.addAndGet((int) dao.saveBeans(separateUserList));
                    synchronized (waitForBranch){
                        if (tmpResult == userList.size())
                            waitForBranch.notify();
                    }
                    synchronized (waitForMain){
                    }
                    if (atomicNeedrollback.get())
                        transactionManager5.rollback(transactionStatus);
                    else
                        transactionManager5.commit(transactionStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                    atomicNeedrollback.set(true);
                    transactionManager5.rollback(transactionStatus);
                    synchronized (waitForBranch){
                        waitForBranch.notify();
                    }
                }
            }
        };
        int cursor = 0;
        while (cursor + 1000 <= userList.size())
            threadList.add(new SaveBatchThread(userList.subList(cursor,cursor += 1000)));
        threadList.add(new SaveBatchThread(userList.subList(cursor,userList.size())));

        synchronized (waitForMain){
            synchronized (waitForBranch){
                for (Thread oneThread : threadList)
                    oneThread.start();
                waitForBranch.wait();
            }
        }

        for (Thread oneThread : threadList)
            oneThread.join();

        logger.debug("-----------批量插入测试END!-----------用时:" + (new Date().getTime() - millis) + "ms");
        return atomicSum.intValue();
    }

    @Override
    public int testInsertBatch2TransactionEnhanced3(List<User2> userList) throws Exception {
        //第四种插入方法的事务增强版_condition+lock+await+signal+signalAll实现(await与signal与signalAll都必须在lock保护之内,即在lock()与unlock()之间)
        //预提交与正式提交分开,当线程数大于数据源连接数时,会报错,因为逻辑是主线程必须等所有分线程的都预提交成功了或者出现预提交失败才释放锁,而分线程又必须等待主线程释放锁后才能正式提交或回滚,
        //      而如果分线程不正式提交或回滚的话,就最多只有最大数据源连接数个线程可以预提交并一直占用这些连接且非空闲状态,其他分线程是不能得到数据源连接的,直到占用的连接超时报错;
        //      因此优化的方向应该是线程数最大只能取最大连接数,然后结合第二第三种方式,分批次提交;
        //这边还有个小问题,因为预提交与正式提交之间有个时间段,所以如果前者成功,mysql服务器瞬间阻塞,后者失败了,那还得手动把已经正式提交的线程插入的数据给删除掉
        logger.debug("-----------批量插入测试START!-----------");
        long millis = new Date().getTime();
        List<Thread> threadList = new LinkedList<>();
        //原子变量,保证多线程操作安全
        AtomicInteger atomicSum = new AtomicInteger(0);
        AtomicBoolean atomicNeedrollback = new AtomicBoolean(false);
        //重入锁,默认非公平锁,效率比公平锁高,公平锁每个线程都有机会执行,排成一个队列AQS(AbstractQueueSynchronizer抽象队列同步器),概率一样,
        // 而非公平锁则利用CAS机制(compare and swap比较与交换)有插队机会,因此等待时间最长的线程最有机会获取锁
        Lock lock = new ReentrantLock();
        Condition waitForBranch = lock.newCondition();
        Condition waitForMain = lock.newCondition();
        //在方法中调用spring的编程式事务管理
        PlatformTransactionManager transactionManager5 = (PlatformTransactionManager) SpringContextsUtil.getBean("transactionManager5", PlatformTransactionManager.class);
        //局部内部类
        class SaveBatchThread extends Thread {

            private List<User2> separateUserList;

            SaveBatchThread(List<User2> tmpUserList){
                separateUserList = tmpUserList;
            }

            @Override
            public void run() {
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus transactionStatus = transactionManager5.getTransaction(def);
                try {
                    int tmpResult = atomicSum.addAndGet((int) dao.saveBeans(separateUserList));
                    lock.lock();
//                    System.out.println("============waitForBranch准备发送signal了哦============");
                    try {
                        if (tmpResult == userList.size())
                            waitForBranch.signal();
//                    System.out.println("============waitForBranch已经发送signal了哦============");
//                    Thread.sleep(5000);
//                    System.out.println("============waitForMain准备await===========");
                        waitForMain.await();
//                    System.out.println("============waitForMain被signalAll===========");
                    } finally {
                        lock.unlock();
                    }
                    if (atomicNeedrollback.get())
                        transactionManager5.rollback(transactionStatus);
                    else
                        transactionManager5.commit(transactionStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                    atomicNeedrollback.set(true);
                    transactionManager5.rollback(transactionStatus);
                    waitForBranch.signal();
                }
            }
        };
        int cursor = 0;
        while (cursor + 1000 <= userList.size())
            threadList.add(new SaveBatchThread(userList.subList(cursor,cursor += 1000)));
        threadList.add(new SaveBatchThread(userList.subList(cursor,userList.size())));

        for (Thread oneThread : threadList)
            oneThread.start();
        lock.lock();
        try {
            //测试一下,这边应该要用while轮询的,否则会出现相互等待唤醒死锁状态;测试结果居然不是!
            // 太神奇了,就好像waitForBranch.signal()必须等待waitForBranch.await先完成,waitForMain.signalAll()必须等待所有的waitForMain.await()先完成一样,
            // 所以这边不需要while(true)来轮询了
            // 查看日志发现,插入时不是多线程同时插入,而是一个个的来;
//            Thread.sleep(10000);
//            System.out.println("================waitForBranch还没await呢===============");
            waitForBranch.await();
//            System.out.println("================waitForBranch被signal了===============");
//            System.out.println("============waitForMain准备发送signalAll===========");
            waitForMain.signalAll();
//            System.out.println("============waitForMain已经发送signalAll===========");
        }finally {
            lock.unlock();
        }

        for (Thread oneThread : threadList)
            oneThread.join();

        logger.debug("-----------批量插入测试END!-----------用时:" + (new Date().getTime() - millis) + "ms");
        return atomicSum.intValue();
    }
}
