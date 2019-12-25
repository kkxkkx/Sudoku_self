import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: sudoku
 * @Description: 进行数独不同局的输出
 * @author WangKeXin
 * @date 2019年12月23日 下午2:00:15
 *
 */
public class sudoku {
	public static int count; // 需要输出数独的个数
	public int curr;// 当前处理的布局位置
	public static final int side = 9;
	public static final int SideSub = 3;
	public static final int init_num = 6;
	public static final int OUTPUT = 0;
	public static final int CALCULATE = 1;
	public static final int BlankHighBound=60;

	public boolean init_state = true; // 是否在输出第一种状态
	public byte[][] layout = null;// 布局
	public byte[] ansFlag = null; // 每个布局位置解空间使用标识（指向下一次要处理的解）
									// 处理过以后变为1
	public byte[] Puz=null;
	public byte[][] ans = null; // 记录每个位置的解空间
	public Random random = new Random();
	public static String PuzzlePath;
	public static int InputType;
	public int BlankNum=0;

	static String temp = "D:\\Learn\\Third\\software Engineering\\Sokudo_self\\Puzzle.txt";

	public static void main(String[] args) {
	    	if(!args[0].equals("-c")&&!args[0].equals("-s")){
	    		PrintError();
	    		return;
	    	}
	    	if(args[0].equals("-s")&&args.length != 3){
	    			PrintError();
	    	        return;  
	    	}
	    	else if(args[0].equals("-c")){
	    		if (args.length != 2) {  
	    			PrintError();
	    	        return;  
	    	    }
	    		if(!isNumeric(args[1])){
	    			PrintError();
		    	}
	    	}
	    
		sudoku sdk = new sudoku();
			if(args[0].equals("-c"))
			{
			    count=Integer.valueOf(args[1]);				
				sdk.generateRandom(count);	
				InputType=OUTPUT;
			}
			else if(args[0].equals("-s"))
			{
				count = 1;
				InputType = CALCULATE;
				sdk.generateRandom(count);
				temp=args[1];
			}
	}
	/**
	* @Title: PrintError
	* @Description: 打印输入错误
	* @param     
	* @return void    
	* @throws
	*/
	private static void PrintError() {
		// TODO Auto-generated method stub
		System.out.println("您的输入不正确\n"); 
        System.out.println("生成终局命令为：java sudoku -c 阿拉伯数字");
        System.out.println("求解数独命令为：java sudoku -s 输出txt的绝对位置");
        return; 		
	}
	/**
	* @Title: isNumeric
	* @Description: 判断输入是否为数字
	* @param  str  
	* @return boolean    
	* @throws
	*/
	public static boolean isNumeric(String str) {
		// 该正则表达式可以匹配所有的数字 包括负数
		Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
		String bigStr;
		try {
			bigStr = new BigDecimal(str).toString();
		} catch (Exception e) {
			return false;// 异常 说明包含非数字。
		}
		Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	* @Title: generateRandom
	* @Description: 把需要输入局面的个数全部初始化
	* @param @param count    
	* @return void    
	* @throws
	*/
	public void generateRandom(int count) {
		if (InputType == CALCULATE) {
			init();
			try {
				readPuzzle(temp);
				SolvePuzzle();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (InputType == OUTPUT)
			for (int i = 0; i < count; i++) {
				init();
				generate();
			}
	}
	
	/**
	* @Title: SolvePuzzle
	* @Description: 解决当前数独
	* @param     
	* @return void    
	* @throws
	*/
	private void SolvePuzzle() {
		// TODO Auto-generated method stub
		FileWriter out = openFileWriter("layout.txt");
        curr = 0;   //当前处理的布局位置	       
        Boolean flag=true;
        while (flag) {
            if (ansFlag[Puz[curr]] == 0)  
                getPosiAnswer(Puz[curr]);  //如果这个位置没有被回溯过，就不用重新计算解空间
            						  //得到可能的解
            int ansCount = getAnswerCount(Puz[curr]);
            if (ansCount == ansFlag[Puz[curr]] && Puz[curr] == 0) // 全部回溯完毕
                break;
            //无可用解
            if (ansCount == 0) {
                ansFlag[Puz[curr]] = 0;
                curr--;
                layout[Puz[curr]/side][Puz[curr]%side] = -1;
                continue;
            }
            // 可用解用完
            else if (ansFlag[Puz[curr]] == ansCount) {
                ansFlag[Puz[curr]] = 0;
                curr--;
                layout[Puz[curr]/side][Puz[curr]%side] = -1;
                continue;
            } else {
                // 返回指定格格中，第几个解
            	layout[Puz[curr]/side][Puz[curr]%side]= getAnswer(Puz[curr], ansFlag[Puz[curr]]);
                ansFlag[Puz[curr++]]++;
            }
            if (Puz[curr]==-1) {
                if (out != null)
                	WriteToFile(out);               	            	
                flag=false;
                curr--;
              //  layout[Puz[curr]/side][Puz[curr]%side] = -1; //最后位置清空
                ansFlag[Puz[curr]] = 1;// 解位置标识请零,人为促使继续回溯
        }
        }
        try {
            out.close();
        } catch (Exception e) {
        }
	}

	/**
	* @Title: readPuzzle
	* @Description: 将Path中的数独局面，读取在layout
	* @param  Path    
	* @return void  FileNotFoundException
	* @throws
	*/
	public void readPuzzle(String Path) throws FileNotFoundException {
		File file = new File(Path);
		BufferedReader buffer = new BufferedReader(new FileReader(file));
		String s = null;
		int row = -1;
		int colomn = 0;
		try {
			s = buffer.readLine();
			while (s != null) {
				row++;
				colomn = 0;
				for (int i = 0; i < s.length(); i++) {
					if (s.charAt(i) == ' ' || s.charAt(i) == '\n')
						continue;
					if (s.charAt(i) == '0') {
						Puz[BlankNum++]=(byte) (row*side+colomn);
						ansFlag[row*side+colomn]=0;
						layout[row][colomn++] = -1;							
						continue;
					} 
					else
					{
						ansFlag[row*side+colomn]=1;
						layout[row][colomn++] = (byte) (s.charAt(i) - '0'-1);					
					}						
				}
				s = buffer.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	* @Title: init
	* @Description: 初始化局面
	* @param     
	* @return void    
	* @throws
	*/
	public void init() {
		if(InputType==CALCULATE)
		{
			Puz=new byte[BlankHighBound];
			for(int i=0;i<BlankHighBound;i++)
				Puz[i]=-1;
		}
			
		if (ansFlag == null)
			ansFlag = new byte[side * side];
		if (ans == null)
			ans = new byte[side * side][side];
		if (layout == null)
			layout = new byte[side][side];

		for (int i = 0; i < side; i++) {
			for (int j = 0; j < side; j++)
				layout[i][j] = -1;// 将布局全部设置为未填状态
			ansFlag[i] = 0;// 用来记录解的位置，回溯时从这个位置往后处理
		}
		for (int i = 0; i < side * side; i++)
			for (int j = 0; j < side; j++)
				ans[i][j] = -1;// 初始化为无解，程序运行中动态求取
	}

	/**
	* @Title: generate
	* @Description: 生成布局
	* @param    
	* @return void    
	* @throws
	*/
	public void generate() {
		FileWriter out = openFileWriter("layout.txt");
		curr = 0; // 当前处理的布局位置
		Boolean flag = true;
		while (flag) {
			if (ansFlag[curr] == 0)
				getPosiAnswer(curr); // 如果这个位置没有被回溯过，就不用重新计算解空间
										// 得到可能的解
			int ansCount = getAnswerCount(curr);
			if (ansCount == ansFlag[curr] && curr == 0) // 全部回溯完毕
				break;
			// 无可用解
			if (ansCount == 0) {
				ansFlag[curr] = 0;
				curr--;
				layout[curr / side][curr % side] = -1;
				continue;
			}
			// 可用解用完
			else if (ansFlag[curr] == ansCount) {
				ansFlag[curr] = 0;
				curr--;
				layout[curr / side][curr % side] = -1;
				continue;
			} else {
				// 返回指定格格中，第几个解
				layout[curr / side][curr % side] = getAnswer(curr, ansFlag[curr]);
				ansFlag[curr++]++;
			}
			if (side * side == curr) {
				if (out != null)
					WriteToFile(out);
				flag = false;
				curr--;
				layout[curr / side][curr % side] = -1; // 最后位置清空
				ansFlag[curr] = 1;// 解位置标识请零,人为促使继续回溯
			}
		}
		try {
			out.close();
		} catch (Exception e) {
		}
	}

	/**
	* @Title: WriteToFile
	* @Description: 将当前布局写入文件
	* @param  fw    
	* @return void    
	* @throws
	*/
	public void WriteToFile(FileWriter fw) {
		try {
			for (int i = 0; i < side; i++) {
				for (int j = 0; j < side; j++) {
					fw.write(String.valueOf(layout[i][j] + 1));
					fw.write(" ");
				}
				fw.write("\n");
			}
			fw.write("\n");
		} catch (Exception e) {
		}
	}

	/**
	* @Title: RandomAnswer
	* @Description:可用随机排序
	* @param curr_temp    
	* @return void    
	* @throws
	*/
	private void RandomAnswer(int curr_temp) {
		// 随机调整一下顺序
		List<Byte> list = new LinkedList<Byte>();
		for (int i = 0; i < side; i++)
			list.add(ans[curr_temp][i]);
		int posi = 0, index = 0;
		if (curr_temp == 0) {
			ans[0][0] = init_num;
			list.remove(init_num);
			index++;
		}
		while (list.size() != 0) {
			// posi是在list中的位置
			posi = Math.abs(random.nextInt()) % list.size();
			ans[curr_temp][index] = list.get(posi);
			list.remove(posi);
			index++;
		}
		list = null;
	}

	/**
	* @Title: getAnswerCount
	* @Description: 获得解的数目
	* @param  curr_temp
	* @return int    
	* @throws
	*/
	private int getAnswerCount(int curr_temp) {
		// 计算可用解的数量
		int count = 0;
		for (int i = 0; i < side; i++)
			if (ans[curr_temp][i] != -1)
				count++;
		return count;
	}

	/**
	* @Title: openFileWriter
	* @Description: 获得一个写文件器，用来输出布局，构造函数的第二个参数true表示追加
	* @param  name  
	* @return FileWriter    
	* @throws
	*/
	private FileWriter openFileWriter(String name) {
		try {
			if (init_state) {
				init_state = false;
				return new FileWriter(name);
			}
			return new FileWriter(name, true);

		} catch (Exception e) {
		}
		return null;
	}

	/**
	* @Title: getPosiAnswer
	* @Description: 返回值指定位置的可用解
	* @param  curr_temp    
	* @return void    
	* @throws
	*/
	private void getPosiAnswer(int curr_temp) {
		for (byte i = 0; i < side; i++)
			ans[curr_temp][i] = i; // 假定包含所有解
		// 在所有可能的解中出去该行或该列已经有的
		int x = curr_temp / side, y = curr_temp % side; // x是当前行，y是当前列
		for (int i = 0; i < side; i++) {
			if (layout[i][y] != -1) // 该列所有数字
				ans[curr_temp][layout[i][y]] = -1; // 删去这个数字
			if (layout[x][i] != -1) // 该行所有数字
				ans[curr_temp][layout[x][i]] = -1;
		}
		// 让在3X3的区域也互不相同
		int x2 = x / SideSub, y2 = y / SideSub; // 该位置在3X3区域的位置
		for (int i = x2 * SideSub; i < SideSub + x2 * SideSub; i++) {
			for (int j = y2 * SideSub; j < SideSub + y2 * SideSub; j++) {
				if (layout[i][j] != -1)
					ans[curr_temp][layout[i][j]] = -1; // 删去在3X3中出现过的数字
			}
		}
		RandomAnswer(curr_temp);
	}

	/**
	* @Title: getAnswer
	* @Description: 得到当前位置可能解的个数
	* @param  curr_temp
	* @param  state
	* @return byte    
	* @throws
	*/
	private byte getAnswer(int curr_temp, int state) {
		int cnt = 0;
		for (int i = 0; i < side; i++) {
			// 找到指定位置的解，返回
			if (cnt == state && ans[curr_temp][i] != -1)
				return ans[curr_temp][i];
			if (ans[curr_temp][i] != -1)
				cnt++;// 是解，调整计数器
		}
		return -1;// 没有找到，逻辑没有问题的话，应该不会出现这个情况
	}
}
