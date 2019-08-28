# TreeViewer
This is going to be an Android app for viewing GEDCOM files (eventually). At present the app only
displays reports which are generated offline using programs from the "lifelines" genealogy program.
The example GEDCOM file come from the "Family Tree Maker (13.0.280)" family tree created by my
father and exported to a GEDCOM format using that program. The family tree database and reports that
I have generated from it using the `llines` program from the lifelines package are in the `Data`
directory and are as follows:
 - `GRAY.FTW` - the original Family Tree Maker data file.
 - `GRAY.ged` - the GEDCOM file exported from the `GRAY.FTW` data file.
 - [2ppage_All_People.txt] - the output of the `2ppage.ll` sample "lifelines" report generating
  program whose description reads: "It will produce a report of all INDI's in the database, with
  two records printed per page. Record 1 and 2 will be on the first page."
 - `4gen1_Ancestor_Report.txt` - the output of the `4gen1.ll` sample "lifelines" report generating
  program using my sister as the starting point, whose description reads: "select and produce an
  ancestor report for the person selected. Output is an ASCII file, and will probably need to be
  using 10 or 12 pitch."
 - [6gen1_Ancestor_Report.txt] - the output of the `6gen1_Ancestor_Report.ll` sample "lifelines"
  report generating program using my sister as the starting point, whose description reads: "select
  and produce a 6 generation ancestor report for the person selected. Output is an ASCII file, and
  will probably need to be printed using 10 or 12 pitch."
 - [lifelines_programs.html] - the list and description of all of the sample lifelines report
  generating included in the "lifelines" genealogy program.

At present the GEDCOM has not been modified in any way from the version exported from the `GRAY.FTW`
data file.
 