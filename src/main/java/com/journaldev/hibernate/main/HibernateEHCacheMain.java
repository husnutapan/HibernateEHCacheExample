package com.journaldev.hibernate.main;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;

import com.husnu.domain.Student;
import com.journaldev.hibernate.util.HibernateUtil;

public class HibernateEHCacheMain {

	public static void main(String[] args) {

		/*
		 * ADD DATA BEFORE CACHING TEST
		 * 
		 * SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		 * Session session = sessionFactory.openSession();
		 * 
		 * session.beginTransaction();
		 * 
		 * Student student = new Student(); student.setName("Jack");
		 * student.setLastName("Ma"); student.setNumber(350);
		 * 
		 * StudentDetail detail = new StudentDetail();
		 * detail.setAddress("CHINA"); detail.setAge(45);
		 * detail.setBirthDate(new Date());
		 * 
		 * student.setStudentDetail(detail); detail.setStudent(student);
		 * 
		 * session.save(student); session.getTransaction().commit();
		 * 
		 * session.close();
		 * 
		 * 
		 */

		System.out.println("Temp Dir:" + System.getProperty("java.io.tmpdir"));

		// Initialize Sessions

		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

		Statistics stats = sessionFactory.getStatistics();

		System.out.println("Stats enabled=" + stats.isStatisticsEnabled());

		stats.setStatisticsEnabled(true);

		System.out.println("Stats enabled=" + stats.isStatisticsEnabled());

		Session session = sessionFactory.openSession();
		Session otherSession = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Transaction otherTransaction = otherSession.beginTransaction();

		printStats(stats, 0);

		Student emp = (Student) session.get(Student.class, 1);
		printData(emp, stats, 1);

		emp = (Student) session.get(Student.class, 1);
		printData(emp, stats, 2);

		// clear first level cache, so that second level cache is used
		session.evict(emp);
		emp = (Student) session.get(Student.class, 1);
		printData(emp, stats, 3);

		// emp = (Student) session.load(Student.class, 3);
		// printData(emp, stats, 4);

		emp = (Student) otherSession.get(Student.class, 1);
		printData(emp, stats, 5);

		// Release resources
		transaction.commit();
		otherTransaction.commit();
		sessionFactory.close();
	}

	private static void printStats(Statistics stats, int i) {
		System.out.println("***** " + i + " *****");
		System.out.println("Fetch Count=" + stats.getEntityFetchCount());
		System.out.println("Second Level Hit Count=" + stats.getSecondLevelCacheHitCount());
		System.out.println("Second Level Miss Count=" + stats.getSecondLevelCacheMissCount());
		System.out.println("Second Level Put Count=" + stats.getSecondLevelCachePutCount());
	}

	private static void printData(Student student, Statistics stats, int count) {
		System.out.println(
				count + ":: Name=" + student.getName() + ", Address=" + student.getStudentDetail().getAddress());
		printStats(stats, count);
	}
}
