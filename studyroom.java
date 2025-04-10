

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


//class representing a study room with room number, total seatts, available seats, and seat numbers.
class Rooms{
    String room_number;
    int total_seats;
    int available_seats;
    List<Integer> seats_int;

    //constuctor to initialize the room details
    Rooms(String room_number,int total_seats,int available_seats,List<Integer> seats_int){
        this.room_number = room_number;
        this.total_seats = total_seats;
        this.available_seats = available_seats;
        this.seats_int = seats_int;
    }
}

//class to store student details when they reserve a seat.
class student_details{
    String index_number;
    Rooms select_room;
    int seat_num_input;

    //constructor to initialize the student details.
    student_details(String index_number, Rooms select_room,int seat_num_input){
        this.index_number=index_number;
        this.select_room=select_room;
        this.seat_num_input=seat_num_input;
    }

    //method to upadte studennt details in the "student.csv" file
    public void update_student_details(){
        String student_file = "student.csv";
        boolean file_exist = new File(student_file).exists();
        try(BufferedWriter bw  = new BufferedWriter(new FileWriter(student_file,true))){
            //if file dosen't exist, write the header
            if(!file_exist){
                bw.write("Student index number,Room number,Seat number,In time,out time\n");
            }
            //get current timestamp for seat reservation time
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            bw.write(this.index_number+","+this.select_room.room_number+","+this.seat_num_input+","+timestamp+"\n");
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }

        System.out.println("Your seat is reserved.");
    }
}

//class to handle seat reservation and update the available seats in the study room.
class reserve_the_seats{
    int seat_num_input;
    Rooms select_room;

    //constructor to initialize seat reservation details.
    reserve_the_seats(int seat_num_input, Rooms select_room){
        this.seat_num_input=seat_num_input;
        this.select_room=select_room;
    }

    //methos to remove a reserved seat from available seats in "study.csv" 
    public void remove_seats(){
        String filename = "study.csv";
        List<String> lines = new ArrayList<>();

        try(BufferedReader br  = new BufferedReader(new FileReader(filename))){
            String line;
            br.readLine();  //skip header line
            while((line=br.readLine())!=null){
                String[] details = line.split(",");
                //check if the selected room matches
                if(this.select_room.room_number.equalsIgnoreCase(details[0])){
                    //remove the reserved seat from available list
                    this.select_room.seats_int.remove(Integer.valueOf(this.seat_num_input));

                    //convert updated seats list to a string
                    String seats_update = "";
                    for(int seat_num: this.select_room.seats_int){
                        seats_update = seats_update + String.valueOf(seat_num)+":";
                    }
                    if(!seats_update.isEmpty()){
                        seats_update = seats_update.substring(0,seats_update.length()-1);
                    }
                    details[3] = seats_update;

                    //update available seats count
                    int update_available = Integer.parseInt(details[2]) - 1;
                    details[2] = String.valueOf(update_available);
                }
                lines.add(String.join(",",details));
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }

        //write updated room details back to "study.csv".
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename))){
            bw.write("room number,total seats,available seats,chair numbers");
            bw.newLine();
            for(String write_line: lines){
                bw.write(write_line);
                bw.newLine();
            }
            
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}

//main class handling the seat reservation system
public class studyroom{
    private static final String filename = "study.csv";
    private static Map<String,Rooms> roomsmap = new HashMap<>();

    public static void main(String args[]){
        Scanner scan = new Scanner(System.in);
        System.out.println("WELCOME TO THE UNIVERSITY OF MORATUWA LIBRARY.");
        System.out.print("Reserve the seat or Release the seat: reserve:1  release:0  : ");
        String reserve_release = scan.nextLine();
        if(reserve_release.equalsIgnoreCase("1")){
            //load room details fromm the csv file
            loadthedetails();
            Rooms select_room = null;
            while(true){
                System.out.print("which number study room you want: Enter 1 to 10: ");
                String input_room_num = scan.nextLine();
                select_room = roomsmap.get(input_room_num);
                if(select_room.total_seats>=select_room.available_seats){
                    System.out.print("Here are the seat numbers available.");
                    for(int j=0; j<select_room.seats_int.size(); j++){
                        System.out.print(select_room.seats_int.get(j)+" ");
                    }
                    System.out.print("Do you want to pick a seat? yes/no: ");
                    String yes_or_no = scan.nextLine();
                    if(yes_or_no.equalsIgnoreCase("yes")){
                        break;
                    }
                    else{
                        System.out.print("Do you want to exit or take a another room? if exit yes: take another room no: ");
                        String exit_or_room = scan.nextLine();
                        if(exit_or_room.equalsIgnoreCase("yes")){
                            return;
                        }
                        else{
                            continue;
                        }
                    }
                    
                }
                else{
                    System.out.println("There is no available seats in this room. you can pick another room.");
                }

            }

            //user selaects a seat
            boolean valid_seat_num = false;
            int seat_num_input;
            while(true){
                System.out.print("Enter the  seat number you want: ");
                seat_num_input = scan.nextInt();
                for(int i=0; i<select_room.seats_int.size(); i++){
                    if(select_room.seats_int.get(i)==seat_num_input){
                        valid_seat_num = true;
                    }
                }

                if(valid_seat_num){
                    break;
                }
                else{
                    System.out.println("There is no this seat number available. Enter the valid seat number which displayed.");
                }
            }
            

            //reserve the seat and updated records
            reserve_the_seats reserve_seat = new reserve_the_seats(seat_num_input,select_room);
            reserve_seat.remove_seats();
            scan.nextLine();
            System.out.print("Enter the index number: ");
            String index_number = scan.nextLine();

            student_details student = new student_details(index_number,select_room,seat_num_input);
            student.update_student_details();
        }
        else{

            //handle seat release process
            System.out.print("Enter your index number: ");
            String input_index_release = scan.nextLine();

            System.out.print("Enter the room number: ");
            String room_number = scan.nextLine();

            System.out.print("Enter the seat number: ");
            String seat_num = scan.nextLine();


            String student_file = "student.csv";
            
            
            List<String> lines = new ArrayList<>();
            List<String> new_student_list = new ArrayList<>();
            String new_append = null;
            try(BufferedReader br = new BufferedReader(new FileReader(student_file))){
                br.readLine();
                String line;
                while((line=br.readLine())!=null){
                    String[] details = line.split(",");
                    if(input_index_release.equalsIgnoreCase(details[0]) && room_number.equalsIgnoreCase(details[1]) && seat_num.equalsIgnoreCase(details[2])){
                        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        lines.add(String.join(",",details)+","+timestamp);
                    }
                    else{
                        lines.add(String.join(",",details));
                    }
                }
            }
            catch(IOException e){
                System.out.println(e.getMessage());
            }
            
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(student_file))){
                bw.write("Student index number,Room number,Seat number,In time, out time\n");
                for(String line: lines){
                    bw.write(line+"\n");
                }
            }
            catch(IOException e){
                System.out.println(e.getMessage());
            }

            String filename = "study.csv";
            List<String> new_lines = new ArrayList<>();
            try(BufferedReader br = new BufferedReader(new FileReader(filename))){
                br.readLine();
                String line;
                while((line=br.readLine())!=null){
                    String[] details = line.split(",");
                    if(details[0].equalsIgnoreCase(room_number)){
                        List<String> seat_list = new ArrayList<>(Arrays.asList(details[3].split(":")));
                        seat_list.add(seat_num);
                        String details_3_str = String.join(":",seat_list);
                        details[3] = details_3_str;
                        int update_available = Integer.parseInt(details[2]) + 1;
                        details[2] = String.valueOf(update_available);
                        new_lines.add(String.join(",",details));

                    }
                    else{
                        new_lines.add(line);
                    }
                }
            }
            catch(IOException e){
                System.out.println(e.getMessage());
            }

            try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename))){
                bw.write("room number,total seats,available seats,chair numbers\n");
                for(String li: new_lines){
                    bw.write(li+"\n");
                }
            }
            catch(IOException e){
                System.out.println(e.getMessage());
            }

        }


    }

    //method to load room details from "study.csv"
    private static void loadthedetails(){
        
        try(BufferedReader bw = new BufferedReader(new FileReader(filename))){
            bw.readLine();
            String line;
            while((line=bw.readLine())!=null){
                String[] details = line.split(",");
                String room_number = details[0];
                int total_seats = Integer.parseInt(details[1]);
                int available_seats = Integer.parseInt(details[2]);
                String[] seats = details[3].split(":");
                List<Integer> seats_int = new ArrayList<>();
                for(String seat: seats){
                    seats_int.add(Integer.parseInt(seat.trim()));
                }

                Rooms room = new Rooms(room_number,total_seats,available_seats,seats_int);
                roomsmap.put(room_number,room);

            }
            
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}
