package com.psl.training;
import java.util.*;
import java.io.*;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.sql.*;


public class Movie implements Serializable,Comparable<Movie>{
	
	private int movieId;
	private String name;
	private String category;
	private String language;
	private String releaseDate;
	private List<String> casting;
	private double ratings;
	private double totalBusinessDone;
	static List<Movie> movie=new ArrayList<>();
	public int getMovieId() {
		return movieId;
	}


	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public String getReleaseDate() {
		return releaseDate;
	}


	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}


	public List<String> getCasting() {
		return casting;
	}


	public void setCasting(List<String> casting) {
		this.casting = casting;
	}


	public double getRatings() {
		return ratings;
	}


	public void setRatings(double ratings) {
		this.ratings = ratings;
	}


	public double getTotalBusinessDone() {
		return totalBusinessDone;
	}


	public void setTotalBusinessDone(double totalBusinessDone) {
		this.totalBusinessDone = totalBusinessDone;
	}


	static List<Movie> populateMovies(File file){
		
		FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br=new BufferedReader(fr);
		
			String str=br.readLine();
			while(str!=null) {
				String[] data=str.split(",");
				Movie m=new Movie();
				
				m.movieId=Integer.parseInt(data[0]);
				m.name=data[1];
				m.category=data[2];
				m.category=data[3];
				m.releaseDate=data[4];
				String[] cast=data[5].split("-");
				List<String> casting1=new ArrayList<String>();
				for(int i=0;i<cast.length;i++) {
					casting1.add(cast[i]);
				}
				
				m.casting=casting1;
				m.ratings=Double.parseDouble(data[6]);
				m.totalBusinessDone=Double.parseDouble(data[7]);
				movie.add(m);
				str=br.readLine();
			}
			
			br.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return movie;
	}
	
	
	public static boolean allMoviesInDb(List<Movie> ls){
		boolean flag = false;
		int t1 =0,t2=0;
		
		try{  
			Class.forName("oracle.jdbc.driver.OracleDriver");   
			Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","123");  
			  
			for(int i=0;i<ls.size();i++) {
				Movie m=ls.get(i);
				PreparedStatement ps =con.prepareStatement("Insert into movie values (?,?,?,?,?,?,?)");
				ps.setInt(1, m.getMovieId());
				ps.setString(2, m.getName());
				ps.setString(3, m.getCategory());
				ps.setString(4, m.getLanguage());
				ps.setString(5, m.getReleaseDate());
				ps.setDouble(6, m.getRatings());
				ps.setDouble(7, m.getTotalBusinessDone());
				t1 = ps.executeUpdate();
				List<String>cast = m.getCasting();
				for(int j=0;j<cast.size();j++) {
					PreparedStatement ps1 = con.prepareStatement("Insert into casting values (?,?)");
					ps1.setInt(1,m.getMovieId());
					ps1.setString(2, cast.get(j).trim());
					t2=ps1.executeUpdate();
				} 
				
				if(t1!=0&&t2!=0) {
					flag = true;
				}
			 }
			}catch(Exception e){ 
				System.out.println(e);
			}
			
		
			
			return flag; 
	}
	
	public static void addMovie(Movie movie,List<Movie>movieList) {
		movieList.add(movie);
	}
	
	public static void serializeMovies(List<Movie> ls,String fileName) {
		File f = new File(fileName);
		
		try {
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(ls);
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static List<Movie> deserializeMovie(String fileName){
		List<Movie> ls = new ArrayList<Movie>();
		Movie movie=null;
		try {
			FileInputStream f=new FileInputStream(fileName);
			ObjectInputStream obj=new ObjectInputStream(f);
			
			ls=(ArrayList<Movie>)obj.readObject();
			obj.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ls;
	}
	
	static List<Movie> getMoviesRealeasedInYear(int year){
		List<Movie> result=new ArrayList<>();
		for(int i=0;i<movie.size();i++) {
			Movie m=movie.get(i);
			int date=Integer.parseInt(m.getReleaseDate().toString().substring(0,4));
			System.out.println(date);
			if(year==date) {
				result.add(m);
			}
		}
		return result;
		
	}
	
	public static List<Movie> getMoviesByActor(String... actorNames){
		List <Movie> m=new ArrayList<Movie>();
		for (Movie ms: movie)
		{
			for (String st:ms.getCasting())
			{
				
				if(Arrays.asList(actorNames).contains(st))
				{
					m.add(ms);
					//break;
				}
			}
		}
		return m;
	}
	
	static void updateMovieRatings(Movie movie,double ratings,List<Movie> movieList) {
		for (Movie m:movieList)
		{
			if(m.equals(movie))
			{
				m.setRatings(ratings);
			}
		}
	}
	static void updateBusiness(Movie movie,double amount,List<Movie> movieList) {
		for(Movie m: movieList) {
			if(m.equals(movie)) {
				m.setTotalBusinessDone(amount);
			}
		}
	}
	
	static void displayMovieList(List<Movie> movieList) {
		Iterator<Movie> it = movieList.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().getName());
		}
	}
	
	static Map<String,Set<Movie>> businessDone(double amount)
	{
		Set<Movie> bus=new TreeSet<Movie>();
		Map<String,Set<Movie>> map=new HashMap<String,Set<Movie>>();
		for (Movie m:movie)
		{
			if(m.getTotalBusinessDone()>amount)
			{
				
				bus.add(m);
				if(map.containsKey(m.getLanguage()))
				{
					map.get(m.getLanguage()).add(m);
				}
				else {
				Set<Movie> bus2=new TreeSet<Movie>();
				bus2.add(m);
				map.put(m.getLanguage(),bus2);	
				}
			}
			
		}
		
		for(Map.Entry m : map.entrySet()){    
			    System.out.println(m.getKey()+" "+m.getValue());    
		}  
	
		return map;
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Movie> movieList;
		movieList=Movie.populateMovies(new File("E:\\Persi\\MovieDetailsAssignment\\src\\com\\psl\\training\\Movies.txt"));
		
		Movie m = new Movie();
		m.setMovieId(4);
		m.setName("Hancock");
		m.setCategory("comedy");
		m.setLanguage("English");
		m.setReleaseDate("2016-06-13");
		String[] arr={"An actor","An actress"};
		m.setCasting(Arrays.asList(arr));
		m.setRatings(3.5);
		m.setTotalBusinessDone(100);
		
		Movie.addMovie(m, movieList);
		displayMovieList(movieList);

		boolean b =Movie.allMoviesInDb(movieList);
		if(b==true) {
			System.out.println("Inserted");
		}else {
			System.out.println(" failed");
		}
		
		List<Movie> yl = getMoviesRealeasedInYear(2019);
		System.out.println("getMoviesRealeasedInYear*****");
		displayMovieList(yl);
		

		List<Movie> al = getMoviesByActor("Kajal","Shahrukh ");
		System.out.println("getMoviesByActor*****");
		displayMovieList(al);
		
	
		updateMovieRatings(m, 3.0, movieList);
		displayMovieList(movieList);
			updateBusiness(m, 150, movieList);
		displayMovieList(movieList);
		
		businessDone(100);

	}


	@Override
	public int compareTo(Movie m) {
		// TODO Auto-generated method stub
		int res=-1;
		int i1=this.getMovieId();
		int i2 = m.getMovieId();
		if(i1>i2) {
			res=1;
		}else if(i1<i2) {
			res=-1;
		}else {
			res=0;
		}
		return res;
	
	}

}
